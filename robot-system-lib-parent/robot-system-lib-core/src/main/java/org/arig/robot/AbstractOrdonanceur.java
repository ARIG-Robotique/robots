package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.AbstractEcranService;
import org.arig.robot.services.AbstractServosService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.RPLidarA2TelemeterOverSocket;
import org.arig.robot.system.group.IRobotGroup;
import org.arig.robot.system.pathfinding.IPathFinder;
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
    protected ResourcePatternResolver patternResolver;

    @Autowired
    protected AbstractRobotStatus robotStatus;

    @Autowired
    protected IIOService ioService;

    @Autowired
    protected AbstractServosService servosService;

    @Autowired
    protected II2CManager i2CManager;

    @Autowired
    protected TrajectoryManager trajectoryManager;

    @Autowired
    protected IPathFinder pathFinder;

    @Autowired
    protected ConvertionRobotUnit conv;

    @Autowired
    protected IMonitoringWrapper monitoringWrapper;

    @Autowired
    protected ILidarTelemeter lidar;

    @Autowired
    protected IRobotGroup group;

    @Autowired
    protected TableUtils tableUtils;

    @Autowired
    protected IAvoidingService avoidingService;

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
     * Avant le début du match
     */
    public abstract void beforeMatch();

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

            initI2C();

            initLidar();

            waitAu();

            waitPower();

            afterInit(); // impl

            initPathfinder();

            initMouvement();

            calageBordure(ecranService.config().isSkipCalageBordure()); // impl

            startLidar();

            beforeMatch(); // impl

            waitTirette();

            match();

            saveMonitoring(startOrdonnanceur);

            cycleFin();

        } catch (ExitProgram e) {
            if (e.isWait()) {
                ThreadUtils.sleep(10000);
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

            // On coupe le jus
            ioService.disableAlim5VPuissance();
            ioService.disableAlim12VPuissance();
        }
    }

    /**
     * Tente de se connecter à l'autre robot
     */
    protected void connectGroup() {
        if (!group.isOpen()) {
            robotStatus.groupOk(group.tryConnect());
        }
    }

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
     * Attends le dévérouillage de l'AU
     */
    private void waitAu() {
        if (!ioService.auOk()) {
            ecranService.displayMessage("L'arrêt d'urgence est coupé", LogLevel.WARN);
            while (!ioService.auOk()) {
                exitFromScreen();
                ThreadUtils.sleep(500);
            }
        }
    }

    /**
     * Attends la puissance et vérifie la tension servos ou emet ExitProgram
     */
    private void waitPower() {
        ecranService.displayMessage("Position de préparation des servos moteurs");
        servosService.cyclePreparation();

        ecranService.displayMessage("Activation puissances 5V et 12V");
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();
        if (!ioService.alimPuissance12VOk() || !ioService.alimPuissance5VOk()) {
            log.warn("Alimentation puissance NOK (12V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk());
            while (!ioService.alimPuissance12VOk() && !ioService.alimPuissance5VOk()) {
                exitFromScreen();
                ThreadUtils.sleep(500);
            }
        }
        ecranService.displayMessage(String.format("Alimentation puissance OK (12V : %s ; 5V : %s)", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk()));
        ThreadUtils.sleep(500);

        double tension = servosService.getTension();
        if (tension < robotConfig.seuilAlimentationServos() && tension > 0) {
            ecranService.displayMessage("/!\\ PROBLEME DE TENSION SERVOS /!\\", LogLevel.ERROR);
            throw new ExitProgram(true);
        }
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

            // Exclusion du petit port pour l'évittement
            tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(890, 0, 1200, 300));

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
        trajectoryManager.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
        trajectoryManager.init();
        robotStatus.enableAsserv();
    }

    /**
     * Démarre le scan du lidar et attente du capot de protection
     */
    private void startLidar() {
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
    private void waitTirette() {
        ecranService.displayMessage("!!! ... ATTENTE DEPART TIRETTE ... !!!");
        while (ioService.tirette()) {
            ThreadUtils.sleep(1);
        }
    }

    /**
     * Execution du match + désactivation puissance et services
     */
    private void match() {
        // Début du compteur de temps pour le match
        robotStatus.startMatch();

        // Match de XX secondes.
        while (robotStatus.matchRunning()) {
            inMatch(); // impl
            ThreadUtils.sleep(200);
        }

        robotStatus.stopMatch();

        afterMatch(); // impl

        ioService.disableAlim12VPuissance();
        lidar.stopScan();
        ecranService.displayMessage(String.format("FIN - Durée match %s ms", robotStatus.getElapsedTime()));

        ioService.disableAlim5VPuissance();
    }

    /**
     * Sauvegarde des data de monitoring
     */
    private void saveMonitoring(final LocalDateTime startOrdonnanceur) {
        ecranService.displayMessage("FIN - Sauvegarde télémétrie");
        monitoringWrapper.save();
        final LocalDateTime stopOrdonnanceur = LocalDateTime.now();
        final File execFile = new File("./logs/" + System.getProperty(IConstantesConfig.keyExecutionId) + ".exec");
        DateTimeFormatter savePattern = DateTimeFormatter.ofPattern(IConstantesConfig.executiondDateFormat);
        List<String> lines = new ArrayList<>();
        lines.add(startOrdonnanceur.format(savePattern));
        lines.add(stopOrdonnanceur.format(savePattern));
        try {
            FileUtils.writeLines(execFile, lines);
            log.info("Création du fichier de fin d'éxécution {}", execFile.getAbsolutePath());
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Attente fin de match
     */
    private void cycleFin() {
        ecranService.displayMessage(
                String.format("FIN - Remettre la tirette et AU pour ejection - Score %s",
                        robotStatus.calculerPoints())
        );

        while (!ioService.tirette() || !ioService.auOk()) {
            ThreadUtils.sleep(1000);
        }

        ioService.enableAlim5VPuissance();

        beforePowerOff(); // impl

        ioService.disableAlim5VPuissance();
    }

    protected void startMonitoring() {
        launchExecId = System.getProperty(IConstantesConfig.keyExecutionId);

        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(IConstantesConfig.executiondIdFormat));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);
        robotStatus.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
    }

    @SneakyThrows
    protected void endMonitoring() {
        monitoringWrapper.save();
        robotStatus.disableForceMonitoring();

        final String execId = System.getProperty(IConstantesConfig.keyExecutionId);

        final File execFile = new File("./logs/" + execId + ".exec");
        DateTimeFormatter execIdPattern = DateTimeFormatter.ofPattern(IConstantesConfig.executiondIdFormat);
        DateTimeFormatter savePattern = DateTimeFormatter.ofPattern(IConstantesConfig.executiondDateFormat);
        List<String> lines = new ArrayList<>();
        lines.add(LocalDateTime.parse(execId, execIdPattern).format(savePattern));
        lines.add(LocalDateTime.now().format(savePattern));
        FileUtils.writeLines(execFile, lines);

        log.info("Création du fichier de fin d'éxécution {}", execFile.getAbsolutePath());

        System.setProperty(IConstantesConfig.keyExecutionId, launchExecId);
    }
}
