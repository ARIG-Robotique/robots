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
import org.arig.robot.filters.common.IntegerChangeFilter;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.EcranService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.motors.AbstractMotor;
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
    private EcranService ecranService;

    @Autowired
    private AbstractMotor motorPavillon;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    public static Ordonanceur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ordonanceur();
        }

        return INSTANCE;
    }

    public void run() throws IOException {
        final LocalDateTime startOrdonnanceur = LocalDateTime.now();
        ecranService.displayMessage("Demarrage de l'ordonancement du match ...");

        try {
            // Bus I2C
            ecranService.displayMessage("Scan I2C");
            i2CManager.executeScan();
        } catch (I2CException e) {
            String error ="Erreur lors du scan I2C";
            ecranService.displayMessage(error, LogLevel.OFF);
            log.error(error, e);
            return;
        }

        HealthInfos lidarHealth = lidar.healthInfo();
        if (!lidarHealth.isOk()) {
            String error = String.format("Status du Lidar KO : %s - %s - Code %s", lidarHealth.getState(), lidarHealth.getValue(), lidarHealth.getErrorCode());
            ecranService.displayMessage(error, LogLevel.ERROR);
            return;
        }

        ecranService.displayMessage("Connexion à la balise");
        short tries = 3;
        do {
            baliseService.tryConnect();
            tries--;
        } while(!baliseService.isConnected() && tries > 0);

        if (!ioService.auOk()) {
            ecranService.displayMessage("L'arrêt d'urgence est coupé", LogLevel.WARN);
            while (!ioService.auOk()) {
                ThreadUtils.sleep(500);
            }
        }
        ecranService.displayMessage("Position de préparation des servos moteurs");
        servosService.cyclePreparation();

        // Activation des puissances
        ecranService.displayMessage("Activation puissances 5V et 12V");
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();
        if (!ioService.alimPuissance12VOk() || !ioService.alimPuissance5VOk()) {
            log.warn("Alimentation puissance NOK (12V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk());
            while (!ioService.alimPuissance12VOk() && !ioService.alimPuissance5VOk()) {
                ThreadUtils.sleep(500);
            }
        }
        ecranService.displayMessage(String.format("Alimentation puissance OK (12V : %s ; 5V : %s)", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk()));
        ThreadUtils.sleep(500);

        // Check tension
        double tension = servosService.getTension();
        if (tension < IConstantesUtiles.SEUIL_BATTERY_VOLTS && tension > 0) {
            ecranService.displayMessage("/!\\ PROBLEME DE TENSION SERVOS /!\\", LogLevel.ERROR);
            ThreadUtils.sleep(10000);
            return;
        }

        ecranService.displayMessage("Choix équipe et lancement calage bordure");
        GetConfigInfos infos;
        IntegerChangeFilter teamChangeFilter = new IntegerChangeFilter(-1);
        do {
            infos = ecranService.config();
            if (teamChangeFilter.filter(infos.getTeam())) {
                robotStatus.setTeam(infos.getTeam());
                log.info("Team {}", robotStatus.getTeam().name());
            }
            ThreadUtils.sleep(500);
        } while(!infos.isStartCalibration());

        ecranService.displayMessage("Chargement de la carte");
        String fileResourcePath = String.format("classpath:maps/sail_the_world-%s.png", robotStatus.getTeam().name());
        final InputStream imgMap = patternResolver.getResource(fileResourcePath).getInputStream();
        pathFinder.construitGraphDepuisImageNoirEtBlanc(imgMap);

//        ecranService.displayMessage("Définition des zones 'mortes' de la carte.");
//        // Exclusion de toutes la zone pente et distributeur personel
//        tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(0, 0, 3000, 457)); // Pente + petit distrib
//        if (robotStatus.getTeam() == Team.BLEU) {
//            // Zone départ adverse Jaune
//            tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(2700, 0, 300, 2000));
//        } else {
//            // Zone d&part adverse Bleu
//            tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(0, 0, 300, 2000));
//        }

        // Initialisation Mouvement Manager
        ecranService.displayMessage("Initialisation du contrôleur de mouvement");
        trajectoryManager.init();

        ecranService.displayMessage("Calage bordure");
        calageBordure(infos.isSkipCalageBordure());

        ecranService.displayMessage("Démarrage du lidar");
        lidar.startScan();

        ScanInfos lidarDatas = lidar.grabDatas();
        if (lidarDatas.getScan().isEmpty()) {
            ecranService.displayMessage("Le capot du lidar est présent");
            while (lidarDatas.getScan().isEmpty()) {
                ThreadUtils.sleep(1000);
                lidarDatas = lidar.grabDatas();
            }
        }

        ecranService.displayMessage("Attente mise de la tirette, choix strategie, mode manuel");
        IntegerChangeFilter strategyChangeFilter = new IntegerChangeFilter(-1);
        while(!ioService.tirette()) {
            infos = ecranService.config();
            if (infos.isModeManuel()) {
                ecranService.displayMessage("!!!! Mode manuel !!!!");
                ThreadUtils.sleep(30000);
            } else if (strategyChangeFilter.filter(infos.getStrategy())) {
                ecranService.displayMessage("Attente mise de la tirette, choix strategie, mode manuel");
                robotStatus.setStrategy(infos.getStrategy());
                log.info("Strategy {}", robotStatus.getStrategy().name());
                positionStrategy();
            }

            ThreadUtils.sleep(500);
        }

        ecranService.displayMessage("!!! ... ATTENTE DEPART TIRRETTE ... !!!");
        while (ioService.tirette()) {
            ThreadUtils.sleep(1);
        }

        // Début du compteur de temps pour le match
        robotStatus.startMatch();

        // Match de XX secondes.
        while (robotStatus.getElapsedTime() < IConstantesNerellConfig.matchTimeMs) {

            // Déclenchement du pavillon
            if (robotStatus.getRemainingTime() <= IConstantesNerellConfig.pavillonRemainingTimeMs && !robotStatus.isPavillon()) {
                log.info("Activation du pavillon");
                motorPavillon.speed(motorPavillon.getMaxSpeed() / 2);
                robotStatus.setPavillon(true);
            }

            ThreadUtils.sleep(200);
        }

        robotStatus.stopMatch();

        servosService.pincesAvantOuvert(false);
        servosService.pincesArriereOuvert(false);
        motorPavillon.speed(motorPavillon.getStopSpeed()); // Ecrit par Nils le 22/03/2020 à 09:47:26

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

        // Attente remise de la tirette pour ejecter le stock
        ecranService.displayMessage("FIN - Remettre la tirette et AU pour ejection");
        while (!ioService.tirette() || !ioService.auOk()) {
            ThreadUtils.sleep(1000);
        }

        ecranService.displayMessage("FIN");

        // Remise en place
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();

        servosService.pincesAvantFerme(false);
        servosService.pincesArriereFerme(false);
        servosService.brasDroitFerme(false);
        servosService.brasGaucheFerme(false);
        servosService.ascenseurAvantRoulage(true);
        servosService.ascenseurArriereHaut(true);
        servosService.pivotArriereFerme(false);
        servosService.moustachesFerme(false);

        ThreadUtils.sleep(1000); // pour attendre les derniers servos avant de couper l'alim

        ioService.disableAlim5VPuissance();
        ioService.disableAlim12VPuissance();
    }

    public void positionStrategy() {
        try {
            if (robotStatus.getStrategy() == EStrategy.AGGRESSIVE) {
                // Aligne vers les boué d'en face'
                if (robotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.alignFrontTo(1730, 800);
                } else {
                    trajectoryManager.alignFrontTo(3000 - 1730, 800);
                }
            } else if (robotStatus.getStrategy() == EStrategy.FINALE) {
                // Blocage robot adverse
                if (robotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.alignFrontTo(1230, 1630);
                } else {
                    trajectoryManager.alignFrontTo(3000 - 1230, 1630);
                }
            } else { // BASIC
                // Aligne vers l'eceuil'
                if (robotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.alignFrontTo(540, 1800);
                } else {
                    trajectoryManager.alignFrontTo(3000 - 540, 1800);
                }
            }

        } catch (AvoidingException e) {
            ecranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
        }
    }

    public void calageBordure(boolean skip) {
        try {
            robotStatus.disableAvoidance();
            robotStatus.enableAsserv();

            trajectoryManager.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientationBasse);

            if (robotStatus.isSimulateur() || skip) {
                if (robotStatus.getTeam() == ETeam.BLEU) {
                    position.setPt(new Point(conv.mmToPulse(200), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setPt(new Point(conv.mmToPulse(3000 - 200), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(180));
                }
            } else {
                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                if (robotStatus.getTeam() == ETeam.BLEU) {
                    position.getPt().setX(conv.mmToPulse(IConstantesNerellConfig.dstCallageY));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.getPt().setX(conv.mmToPulse(3000 - IConstantesNerellConfig.dstCallageY));
                    position.setAngle(conv.degToPulse(180));
                }

                trajectoryManager.avanceMM(150);
                trajectoryManager.gotoOrientationDeg(-90);

                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMM(1000);

                position.getPt().setY(conv.mmToPulse(2000 - IConstantesNerellConfig.dstCallageY));
                position.setAngle(conv.degToPulse(-90));

                trajectoryManager.avanceMM(150);

                if (robotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.gotoPointMM(200, 1200, true);
                } else {
                    trajectoryManager.gotoPointMM(3000 - 200, 1200, true);
                }
            }
        } catch (AvoidingException e) {
            ecranService.displayMessage("Erreur lors du calage bordure", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer pour le départ", e);
        }
    }
}
