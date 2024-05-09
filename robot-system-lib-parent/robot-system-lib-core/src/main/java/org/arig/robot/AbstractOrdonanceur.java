package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractBaliseService;
import org.arig.robot.services.AbstractEcranService;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.AbstractServosService;
import org.arig.robot.services.IOService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.arig.robot.system.capteurs.socket.RPLidarA2TelemeterOverSocket;
import org.arig.robot.system.pathfinding.PathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractOrdonanceur {

    @Autowired
    protected RobotConfig robotConfig;

    @Autowired
    protected RobotName robotName;

    @Autowired
    protected ResourcePatternResolver patternResolver;

    @Autowired
    protected AbstractRobotStatus robotStatus;

    @Autowired
    protected AbstractEnergyService energyService;

    @Autowired
    protected IOService io;

    @Autowired
    protected AbstractServosService servos;

    @Autowired
    protected I2CManager i2CManager;

    @Autowired
    protected TrajectoryManager mv;

    @Autowired
    protected PathFinder pathFinder;

    @Autowired
    protected ConvertionRobotUnit conv;

    @Autowired
    protected MonitoringWrapper monitoringWrapper;

    @Autowired
    protected ILidarTelemeter lidar;

    @Autowired
    protected AbstractBaliseService<?> baliseService;

    @Autowired
    protected TableUtils tableUtils;

    @Autowired
    protected AvoidingService avoidingService;

    @Autowired
    protected AbstractEcranService ecranService;

    @Autowired
    @Qualifier("currentPosition")
    protected Position position;

    protected String launchExecId;

    /**
     * Construit le chemin de la map du pathfinder dans le classpath
     */
    public abstract String getPathfinderMap();

    /**
     * Après initialisation comm + puissance
     */
    public abstract void afterInit();

    /**
     * Calage bordure
     */
    public abstract void calageBordure(boolean skip);

    /**
     * Ajout des dead zones
     */
    public abstract void addDeadZones();

    /**
     * Avant le début du match
     */
    public abstract void beforeMatch(boolean skip);

    /**
     * Juste au début du match
     */
    public abstract void startMatch();

    /**
     * Pendant le match
     */
    public abstract void inMatch();

    /**
     * Juste à la fin du match
     */
    public abstract void afterMatch();

    /**
     * Avant l'arrêt
     */
    public abstract void beforePowerOff();

    public final void run() {
        try {
            final LocalDateTime startOrdonnanceur = LocalDateTime.now();
            ecranService.displayMessage("Demarrage de l'ordonancement du match ...");

            initRun();

            initI2C();

            initLidar();

            waitAu();

            waitPower();

            afterInit(); // impl

            initPathfinder();

            initMouvement();

            calageBordure(ecranService.config().isSkipCalageBordure()); // impl

            startLidar();

            addDeadZones();

            beforeMatch(ecranService.config().isSkipCalageBordure()); // impl

            ecranService.displayMessage("!!! ... ATTENTE DEPART TIRETTE ... !!!");
            robotStatus.waitTirette(true);
            if (!ecranService.isOK()) {
                log.warn("Démarrage de match sans écran");
                robotStatus.ecranEnabled(false);
            }
            while (waitTirette()) {
                ThreadUtils.sleep(1);
            }
            robotStatus.waitTirette(false);

            match();

            saveMonitoring(startOrdonnanceur);

            cycleFin();

        } catch (ExitProgram e) {
            if (e.isWait()) {
                ThreadUtils.sleep(1000);
            }
        } catch (Exception e) {
            log.error("Gros catch de fin, sans comprendre pourquoi", e);
        } finally {
            // Désactivation des taches communicante
            robotStatus.disableMainThread();
            robotStatus.disableAvoidance();
            robotStatus.disableAsserv();
            ThreadUtils.sleep(500);

            lidar.stopScan();
            baliseService.stopDetection();

            // On coupe le jus
            io.disableAlimServos();
            io.disableAlimMoteurs();
        }
    }

    /**
     * Tente de se connecter aux autres robots
     */
    protected abstract void connectGroups();

    /**
     * Emet ExitProgram à la demande de l'écran
     */
    protected void exitFromScreen() {
        if (ecranService.config() != null && ecranService.config().isExit()) {
            ecranService.displayMessage("Arret du programme");
            throw new ExitProgram(true);
        }
    }

    /**
     * Initialisation du robot
     */
    protected void initRun() {
    }

    /**
     * Initialise le bus I2C ou emet ExitProgram
     */
    private void initI2C() {
        try {
            ecranService.displayMessage("Scan I2C");
            i2CManager.executeScan();
        } catch (I2CException e) {
            String error = "Erreur lors du scan I2C";
            ecranService.displayMessage(error, LogLevel.OFF);
            log.error(error, e);
            throw new ExitProgram(true);
        }
    }

    /**
     * Valide le statut du lidar ou emet ExitProgram
     */
    private void initLidar() {
        HealthInfos lidarHealth = lidar.healthInfo();
        if (!lidarHealth.isOk()) {
            String error = String.format("Status du Lidar KO : %s - %s - Code %s", lidarHealth.getState(), lidarHealth.getValue(), lidarHealth.getErrorCode());
            ecranService.displayMessage(error, LogLevel.ERROR);
            throw new ExitProgram(true);
        }
    }

    /**
     * Prend en compte la config de la balise
     */
    protected void configBalise(String team) {
        baliseService.setTeam(team);
    }

    protected void customWaitAu() {}

    /**
     * Attends le dévérouillage de l'AU
     */
    private void waitAu() {
        if (!io.auOk()) {
            ecranService.displayMessage("L'arrêt d'urgence est coupé", LogLevel.WARN);
            while (!io.auOk()) {
                exitFromScreen();
                customWaitAu();
                ThreadUtils.sleep(1000);
            }
        }
    }

    /**
     * Attends la puissance et vérifie la tension servos ou emet ExitProgram
     */
    private void waitPower() {
        ecranService.displayMessage("Position de préparation des servos moteurs");
        servos.cyclePreparation();

        ecranService.displayMessage("Activation puissances servos et moteurs");
        io.enableAlimServos();
        ThreadUtils.sleep(1000);
        io.enableAlimMoteurs();
        ThreadUtils.sleep(1000);
        int nbTryMoteurs = 0;
        int nbTryServos = 0;
        if (!energyService.checkMoteurs() || !energyService.checkServos()) {
            ecranService.displayMessage(String.format("Alim. NOK (Moteurs (%d) : %s V ; Servos (%d) : %s V)",
                    nbTryMoteurs, energyService.tensionMoteurs(), nbTryServos, energyService.tensionServos()));
            while (!energyService.checkMoteurs() || !energyService.checkServos()) {
                exitFromScreen();
                ThreadUtils.sleep(500);
                if (!energyService.checkServos() && nbTryServos < 3) {
                    nbTryServos++;
                    log.warn("Rentative alim. servos {}/3", nbTryServos);
                    io.disableAlimServos();
                    ThreadUtils.sleep(100);
                    io.enableAlimServos();
                    ThreadUtils.sleep(100);
                }
                if (!energyService.checkMoteurs() && nbTryMoteurs < 3) {
                    nbTryMoteurs++;
                    log.warn("Rentative alim. moteurs {}/3", nbTryServos);
                    io.disableAlimMoteurs();
                    ThreadUtils.sleep(100);
                    io.enableAlimMoteurs();
                    ThreadUtils.sleep(100);
                }
            }
        }
        ecranService.displayMessage(String.format("Alim. OK (Moteurs (%d) : %s V ; Servos (%d) : %s V)",
                nbTryMoteurs, energyService.tensionMoteurs(), nbTryServos, energyService.tensionServos()));
        ThreadUtils.sleep(500);
    }

    /**
     * Initialise le pathfinder ou emet ExitProgram
     */
    private void initPathfinder() {
        ecranService.displayMessage("Initialisation pathfinder");

        try (final InputStream imgMap = patternResolver.getResource(getPathfinderMap()).getInputStream()) {
            pathFinder.construitGraphDepuisImageNoirEtBlanc(imgMap);

            try {
                // path bidon pour que l'init de l'algo se fasse
                pathFinder.findPath(new Point(150, 100), new Point(150, 110));
            } catch (NoPathFoundException e) {
                log.warn(e.getMessage());
            }
        } catch (IOException e) {
            ecranService.displayMessage("Erreur d'init du pathfinder", LogLevel.ERROR);
            throw new ExitProgram(true);
        }
    }

    /**
     * Initialise le mouvement manager
     */
    private void initMouvement() {
        ecranService.displayMessage("Initialisation du contrôleur de mouvement");
        mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
        mv.init();
        robotStatus.enableAsserv();
    }

    /**
     * Démarre le scan du lidar et attente du capot de protection
     */
    protected void startLidar() {
        ecranService.displayMessage("Démarrage du lidar");
        lidar.startScan(RPLidarA2TelemeterOverSocket.MAX_MOTOR_PWM);

        ScanInfos lidarData = lidar.grabData();
        if (lidarData.getScan().isEmpty()) {
            ecranService.displayMessage("Le capot du lidar est présent");
            while (lidarData.getScan().isEmpty()) {
                ThreadUtils.sleep(1000);
                lidarData = lidar.grabData();
            }

            for (int i = 0; i < 5; i++) {
                ecranService.displayMessage(String.format("Attente %ss pour remettre la capot si besoin", 5 - i));
                ThreadUtils.sleep(1000);
            }
        }
    }

    /**
     * Attente du départ du match
     */
    protected boolean waitTirette() {
        return io.tirette();
    }

    /**
     * Execution du match + désactivation puissance et services
     */
    private void match() {
        // Début du compteur de temps pour le match
        robotStatus.startMatch();

        startMatch(); // impl

        // Match de XX secondes.
        while (robotStatus.matchRunning()) {
            inMatch(); // impl
            ThreadUtils.sleep(200);
        }

        robotStatus.stopMatch();

        afterMatch(); // impl

        io.disableAlimMoteurs();
        lidar.stopScan();
        ecranService.displayMessage(String.format("FIN - Durée match %s ms", robotStatus.getElapsedTime()));

        io.disableAlimServos();

        log.info("SCORE");
        robotStatus.scoreStatus().forEach((key, val) -> {
            log.info("  - {} : {}", key, val);
        });
        log.info("FLAGS");
        robotStatus.gameFlags().forEach((key, val) -> {
            log.info("  - {} : {}", key, val);
        });
        log.info("STATUS");
        robotStatus.gameStatus().forEach((key, val) -> {
            log.info("  - {} : {}", key, val);
        });
        log.info("DEPOSES");
        robotStatus.deposesStatus().forEach((key, val) -> {
            log.info("  - {} : {}", key, val);
        });
    }

    /**
     * Sauvegarde des data de monitoring
     */
    private void saveMonitoring(final LocalDateTime startOrdonnanceur) {
        ecranService.displayMessage("FIN - Sauvegarde télémétrie");
        monitoringWrapper.save();
        final LocalDateTime stopOrdonnanceur = LocalDateTime.now();
        final File execFile = new File("./logs/" + System.getProperty(ConstantesConfig.keyExecutionId) + ".exec");
        DateTimeFormatter savePattern = DateTimeFormatter.ofPattern(ConstantesConfig.executiondDateFormat);
        List<String> lines = new ArrayList<>();
        lines.add(startOrdonnanceur.format(savePattern));
        lines.add(stopOrdonnanceur.format(savePattern));
        try {
            FileUtils.writeLines(execFile, lines);
            log.info("Création du fichier de fin d'exécution {}", execFile.getAbsolutePath());
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Attente fin de match
     */
    protected void cycleFin() {
        ecranService.displayMessage(
                String.format("FIN - Tirette et AU OK pour ejection stock - Score %s",
                        robotStatus.calculerPoints())
        );

        while (!io.tirette() || !io.auOk()) {
            ThreadUtils.sleep(1000);
        }

        beforePowerOff(); // impl
    }

    protected void startMonitoring() {
        launchExecId = System.getProperty(ConstantesConfig.keyExecutionId);

        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat));
        System.setProperty(ConstantesConfig.keyExecutionId, execId);
        robotStatus.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
    }

    @SneakyThrows
    protected void endMonitoring() {
        monitoringWrapper.save();
        robotStatus.disableForceMonitoring();

        final String execId = System.getProperty(ConstantesConfig.keyExecutionId);

        final File execFile = new File("./logs/" + execId + ".exec");
        DateTimeFormatter execIdPattern = DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat);
        DateTimeFormatter savePattern = DateTimeFormatter.ofPattern(ConstantesConfig.executiondDateFormat);
        List<String> lines = new ArrayList<>();
        lines.add(LocalDateTime.parse(execId, execIdPattern).format(savePattern));
        lines.add(LocalDateTime.now().format(savePattern));
        FileUtils.writeLines(execFile, lines);

        log.info("Création du fichier de fin d'exécution {}", execFile.getAbsolutePath());

        System.setProperty(ConstantesConfig.keyExecutionId, launchExecId);
    }
}
