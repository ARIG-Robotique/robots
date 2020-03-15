package org.arig.robot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesUtiles;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.capteurs.IEcran;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.process.StreamGobbler;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Ordonanceur {

    private static Ordonanceur INSTANCE;

    @Autowired
    private ResourcePatternResolver patternResolver;

    @Autowired
    private RobotStatus robotStatus;

    @Autowired
    private IIOService ioService;

    @Autowired
    private II2CManager i2CManager;

    @Autowired
    private ServosService servosService;

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private IPathFinder pathFinder;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Autowired
    private ILidarTelemeter lidar;

    @Autowired
    private TableUtils tableUtils;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private IEcran ecran;

    private UpdateStateInfos screenState = new UpdateStateInfos();
    private UpdateMatchInfos matchInfos = new UpdateMatchInfos();

    public static Ordonanceur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ordonanceur();
        }

        return INSTANCE;
    }

    public void run() throws IOException {
        final LocalDateTime startOrdonnanceur = LocalDateTime.now();
        displayScreenMessage("Demarrage de l'ordonancement du match ...");

        try {
            // Bus I2C
            displayScreenMessage("Scan I2C");
            i2CManager.executeScan();
        } catch (I2CException e) {
            String error ="Erreur lors du scan I2C";
            displayScreenMessage(error, LogLevel.OFF);
            log.error(error, e);
            return;
        }
        screenState.setI2c(true);
        updateScreenState();

        HealthInfos lidarHealth = lidar.healthInfo();
        if (!lidarHealth.isOk()) {
            String error = String.format("Status du Lidar KO : %s - %s - Code %s", lidarHealth.getState(), lidarHealth.getValue(), lidarHealth.getErrorCode());
            displayScreenMessage(error, LogLevel.ERROR);
            return;
        }
        screenState.setLidar(true);
        updateScreenState();

        if (!ioService.auOk()) {
            displayScreenMessage("L'arrêt d'urgence est coupé", LogLevel.WARN);
            while (!ioService.auOk()) {
                ThreadUtils.sleep(500);
            }
        }
        displayScreenMessage("Arrêt d'urgence OK");
        screenState.setAu(true);
        updateScreenState();

        displayScreenMessage("Position de préparation des servos moteurs");
        servosService.cyclePreparation();

        // Activation des puissances
        displayScreenMessage("Activation puissances 5V et 12V");
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();
        if (!ioService.alimPuissance12VOk() || !ioService.alimPuissance5VOk()) {
            log.warn("Alimentation puissance NOK (12V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk());
            while (!ioService.alimPuissance12VOk() && !ioService.alimPuissance5VOk()) {
                ThreadUtils.sleep(500);
            }
        }
        screenState.setAlim5vp(true);
        screenState.setAlim12v(true);
        updateScreenState();
        log.info("Alimentation puissance OK (12V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk());

        // Check tension
        double tension = servosService.getTension();
        if (tension < IConstantesUtiles.SEUIL_BATTERY_VOLTS && tension > 0) {
            displayProblemeTension(tension);
            return;
        }

        displayScreenMessage("Choix équipe, strategy et lancement calibration");
        GetConfigInfos infos;
        do {
            infos = ecran.configInfos();
            log.info("Team {} ; Strategy {} ; Calibration {}", infos.getTeam(), infos.getStrategy(), infos.isStartCalibration());
            ThreadUtils.sleep(500);
        } while(!infos.isStartCalibration());

        robotStatus.setTeam(infos.getTeam() == 1 ? Team.JAUNE : Team.BLEU);

        log.info("Equipe : {}", robotStatus.getTeam().name());
        //List<EStrategy> strategies = ioService.strategies();
        //log.info("Stratégies actives : {}", strategies);

        displayScreenMessage("Chargement de la carte");
        String fileResourcePath = String.format("classpath:maps/%s.png", robotStatus.getTeam().name().toLowerCase());
        final InputStream imgMap = patternResolver.getResource(fileResourcePath).getInputStream();
        pathFinder.construitGraphDepuisImageNoirEtBlanc(imgMap);

        displayScreenMessage("Définition des zones 'mortes' de la carte.");
        // Exclusion de toutes la zone pente et distributeur personel
        tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(0, 0, 3000, 457)); // Pente + petit distrib
        if (robotStatus.getTeam() == Team.BLEU) {
            // Zone départ adverse Jaune
            tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(0, 0, 300, 2000));
        } else {
            // Zone d&part adverse Bleu
            tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(2700, 0, 300, 2000));
        }

        // Initialisation Mouvement Manager
        displayScreenMessage("Initialisation du contrôleur de mouvement");
        trajectoryManager.init();

        displayScreenMessage("Calage bordure");
        calageBordure();

        displayScreenMessage("Démarrage du lidar");
        lidar.startScan();

        displayScreenMessage("Connexion à la balise");
        short tries = 3;
        do {
            baliseService.tryConnect();
            tries--;
        } while(!baliseService.isConnected() && tries > 0);
        screenState.setBalise(baliseService.isConnected());
        updateScreenState();

        displayScreenMessage("Attente mise de la tirette");
        while(!ioService.tirette()) {
            ThreadUtils.sleep(100);
        }
        screenState.setTirette(true);
        updateScreenState();

        displayDepart();
        while (ioService.tirette()) {
            ThreadUtils.sleep(1);
        }

        // Début du compteur de temps pour le match
        robotStatus.startMatch();

        // Match de XX secondes.
        while (robotStatus.getElapsedTime() < IConstantesNerellConfig.matchTimeMs) {
            matchInfos.setScore(robotStatus.calculerPoints());
            matchInfos.setMessage(String.format("%s (%s restantes) - %s s", strategyManager.getCurrentAction(), strategyManager.actionsCount(), robotStatus.getRemainingTime() / 1000));
            updateMatchState();
            ThreadUtils.sleep(200);
        }

        robotStatus.stopMatch();

        servosService.pincesAvantOuvert(false);
        servosService.pincesArriereOuvert(false);

        log.info("Fin de l'ordonancement du match. Durée {} ms", robotStatus.getElapsedTime());

        // Désactivation de la puissance moteur pour être sur de ne plus rouler
        ioService.disableAlim5VPuissance();
        ioService.disableAlim12VPuissance();

        // On arrette le lidar
        lidar.stopScan();

        // On envoi les datas collecté
        monitoringWrapper.save();
        final LocalDateTime stopOrdonnanceur = LocalDateTime.now();
        final File execFile = new File("./logs/" + System.getProperty(IConstantesConfig.keyExecutionId) + ".exec");
        DateTimeFormatter savePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> lines = new ArrayList<>();
        lines.add(startOrdonnanceur.format(savePattern));
        lines.add(stopOrdonnanceur.format(savePattern));
        FileUtils.writeLines(execFile, lines);
        log.info("Création du fichier de fin d'éxécution {}", execFile.getAbsolutePath());

        // Visualisation du score
        int score = robotStatus.calculerPoints();

        // Attente remise de la tirette pour ejecter les palets en stock
        while (!ioService.tirette() || !ioService.auOk()) {
            displayScore(score);
            ThreadUtils.sleep(1000);
        }

        // Remise en place
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();

        servosService.pincesAvantFerme(false);
        servosService.pincesArriereFerme(false);
        servosService.poussoirDroiteFerme(false);
        servosService.poussoirGaucheFerme(false);
        servosService.ascenseurAvantHaut(true);
        servosService.ascenseurArriereHaut(true);
        servosService.pivotArriereFerme(false);
        servosService.moustachesFerme(false);

        ThreadUtils.sleep(1000); // pour attendre les derniers servos avant de couper l'alim

        ioService.disableAlim5VPuissance();
        ioService.disableAlim12VPuissance();
    }

    public void calageBordure() {
        try {
            robotStatus.disableAvoidance();
            robotStatus.enableAsserv();

            trajectoryManager.setVitesse(IConstantesNerellConfig.vitesseUltraLente, IConstantesNerellConfig.vitesseOrientationSuperBasse);

            if (!robotStatus.isSimulateur()) {
                robotStatus.enableCalageBordureArriere();
                trajectoryManager.reculeMMSansAngle(1000);

                if (robotStatus.getTeam() == Team.JAUNE) {
                    position.getPt().setX(conv.mmToPulse(IConstantesNerellConfig.dstArriere));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.getPt().setX(conv.mmToPulse(3000 - IConstantesNerellConfig.dstArriere));
                    position.setAngle(conv.degToPulse(180));
                }

                trajectoryManager.avanceMM(150);
                trajectoryManager.gotoOrientationDeg(-90);

                robotStatus.enableCalageBordureArriere();
                trajectoryManager.reculeMM(1000);

                position.getPt().setY(conv.mmToPulse(2000 - IConstantesNerellConfig.dstArriere));
                position.setAngle(conv.degToPulse(-90));

                trajectoryManager.avanceMM(150);

                if (robotStatus.getTeam() == Team.JAUNE) {
                    trajectoryManager.gotoPointMM(250, 1500, true);
                } else {
                    trajectoryManager.gotoPointMM(2750, 1500, true);
                }

                // Aligne vers le distributeur centre
                if (robotStatus.getTeam() == Team.JAUNE) {
                    trajectoryManager.alignFrontTo(750, 700);
                } else {
                    trajectoryManager.alignFrontTo(2250, 700);
                }
            } else {
                if (robotStatus.getTeam() == Team.JAUNE) {
                    position.setPt(new Point(conv.mmToPulse(250), conv.mmToPulse(1500)));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setPt(new Point(conv.mmToPulse(2750), conv.mmToPulse(1500)));
                    position.setAngle(conv.degToPulse(180));
                }
            }
        } catch (AvoidingException e) {
            throw new RuntimeException("Impossible de se placer pour le départ", e);
        }
    }

    private void displayProblemeTension(double tension) {
        String message = "/!\\ PROBLEME DE TENSION SERVOS /!\\";
        displayScreenMessage(message, LogLevel.OFF);
        try {
            ProcessBuilder pb = new ProcessBuilder("figlet", "-f", "big", "\n" + message + "\n");
            Process p = pb.start();

            StreamGobbler out = new StreamGobbler(p.getInputStream(), System.out::println);
            new Thread(out).start();
        } catch (IOException e) {
            log.error("/!\\ PROBLEME DE TENSION SERVOS /!\\");
        }
    }

    private void displayDepart() {
        displayScreenMessage("READY");
        try {
            ProcessBuilder pb = new ProcessBuilder("figlet", "-f", "big", "\n/!\\ READY /!\\\n");
            Process p = pb.start();

            StreamGobbler out = new StreamGobbler(p.getInputStream(), System.out::println);
            new Thread(out).start();
        } catch (IOException e) {
            log.info("!!! ... ATTENTE DEPART TIRRETTE ... !!!");
        }
    }

    private void displayScreenMessage(String message) {
        displayScreenMessage(message, LogLevel.INFO);
    }

    private void displayScreenMessage(String message, LogLevel logLevel) {
        if (logLevel == LogLevel.INFO) log.info(message);
        else if (logLevel == LogLevel.WARN) log.warn(message);
        else if (logLevel == LogLevel.ERROR) log.error(message);

        screenState.setMessage(message);
        updateScreenState();
    }

    private void updateScreenState() {
        ecran.updateState(screenState);
    }

    private void updateMatchState() {
        ecran.updateMatch(matchInfos);
    }

    private void displayScore(int score) {
        matchInfos.setScore(score);
        matchInfos.setMessage("FIN - Remettre la tirette et AU pour ejection");
        updateMatchState();
        try {
            ProcessBuilder pb = new ProcessBuilder("figlet", "-f", "big", String.format("\n\n\n\nScore : %d\n", score));
            Process p = pb.start();

            StreamGobbler out = new StreamGobbler(p.getInputStream(), System.out::println);
            StreamGobbler err = new StreamGobbler(p.getErrorStream(), log::error);
            new Thread(out).start();
            new Thread(err).start();
        } catch (IOException e) {
            log.info("Score : {}", score);
        }
    }
}
