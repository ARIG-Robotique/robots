package org.arig.robot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesUtiles;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
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
public class NerellOrdonanceur {

    private static NerellOrdonanceur INSTANCE;

    @Autowired
    private ResourcePatternResolver patternResolver;

    @Autowired
    private NerellRobotStatus nerellRobotStatus;

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

    private String launchExecId;

    public static NerellOrdonanceur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NerellOrdonanceur();
        }

        return INSTANCE;
    }

    public void run() throws IOException {
        try {
            final LocalDateTime startOrdonnanceur = LocalDateTime.now();
            ecranService.displayMessage("Demarrage de l'ordonancement du match ...");

            try {
                // Bus I2C
                ecranService.displayMessage("Scan I2C");
                i2CManager.executeScan();
            } catch (I2CException e) {
                String error = "Erreur lors du scan I2C";
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

            ecranService.displayMessage("Connexion à la balise");
            short tries = 3;
            do {
                baliseService.tryConnect();
                tries--;
            } while (!baliseService.isConnected() && tries > 0);

            ecranService.displayMessage("Choix équipe et lancement calage bordure");
            ChangeFilter<Integer> teamChangeFilter = new ChangeFilter<>(-1);
            do {
                if (teamChangeFilter.filter(ecranService.config().getTeam())) {
                    nerellRobotStatus.setTeam(ecranService.config().getTeam());
                    log.info("Team {}", nerellRobotStatus.getTeam().name());
                }
                if (ecranService.config().isExit()) {
                    log.info("Arret du programme");
                    return;
                }
                ThreadUtils.sleep(500);
            } while (!ecranService.config().isStartCalibration());

            ecranService.displayMessage("Activation de la balise");
            nerellRobotStatus.enableBalise();

            ecranService.displayMessage("Initialisation pathfinder");
            String fileResourcePath = String.format("classpath:maps/sail_the_world-%s-nochenal.png", nerellRobotStatus.getTeam().name());
            final InputStream imgMap = patternResolver.getResource(fileResourcePath).getInputStream();
            pathFinder.construitGraphDepuisImageNoirEtBlanc(imgMap);
            try {
                // path bidon pour que l'init de l'algo se fasse
                pathFinder.findPath(new Point(150, 100), new Point(150, 110));
            } catch (NoPathFoundException e) {
                log.warn(e.getMessage());
            }

            // Exclusion du petit port pour l'évittement
            tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(890, 0, 1200, 300));

            // Initialisation Mouvement Manager
            ecranService.displayMessage("Initialisation du contrôleur de mouvement");
            trajectoryManager.setVitesse(IConstantesNerellConfig.vitesseUltraLente, IConstantesNerellConfig.vitesseOrientationBasse);
            trajectoryManager.init();
            nerellRobotStatus.enableAsserv();

            ecranService.displayMessage("Calage bordure");
            calageBordure(ecranService.config().isSkipCalageBordure());

            ecranService.displayMessage("Démarrage du lidar");
            lidar.startScan();

            ScanInfos lidarDatas = lidar.grabDatas();
            if (lidarDatas.getScan().isEmpty()) {
                ecranService.displayMessage("Le capot du lidar est présent");
                while (lidarDatas.getScan().isEmpty()) {
                    ThreadUtils.sleep(1000);
                    lidarDatas = lidar.grabDatas();
                }

                for (int i = 0; i < 10; i++) {
                    ecranService.displayMessage(String.format("Attente %ss pour remettre la capot si besoin", 10 - i));
                    ThreadUtils.sleep(1000);
                }
            }
            ecranService.displayMessage("Attente mise de la tirette, choix strategie ou mode manuel");

            SignalEdgeFilter manuelRisingEdge = new SignalEdgeFilter(ecranService.config().isModeManuel(), Type.RISING);
            SignalEdgeFilter manuelFallingEdge = new SignalEdgeFilter(ecranService.config().isModeManuel(), Type.FALLING);
            ChangeFilter<Integer> strategyChangeFilter = new ChangeFilter<>(-1);
            boolean manuel = ecranService.config().isModeManuel();
            while (!ioService.tirette()) {
                if (manuelRisingEdge.filter(ecranService.config().isModeManuel())) {
                    manuel = true;
                    strategyChangeFilter.filter(-1);
                    ecranService.displayMessage("!!!! Mode manuel !!!!");
                    startMonitoring();
                } else if (manuel && manuelFallingEdge.filter(ecranService.config().isModeManuel())) {
                    manuel = false;
                    endMonitoring();
                    ecranService.displayMessage("Attente mise de la tirette, choix strategie ou mode manuel");
                }

                // Si on est pas en manuel, gestion de la strategy
                if (!manuel && !ecranService.config().isSkipCalageBordure()) {
                    ecranService.displayMessage("Attente mise de la tirette, choix strategie ou mode manuel");
                    if (strategyChangeFilter.filter(ecranService.config().getStrategy())) {
                        nerellRobotStatus.setStrategy(ecranService.config().getStrategy());
                        log.info("Strategy {}", nerellRobotStatus.getStrategy().name());
                        positionStrategy();
                    }
                }

                if (ecranService.config().isExit()) {
                    log.info("Arret du programme");
                    return;
                }

                ThreadUtils.sleep(manuel ? 4000 : 500);
            }

            ecranService.displayMessage("!!! ... ATTENTE DEPART TIRETTE ... !!!");
            while (ioService.tirette()) {
                ThreadUtils.sleep(1);
            }

            // Début du compteur de temps pour le match
            nerellRobotStatus.startMatch();

            // Match de XX secondes.
            while (nerellRobotStatus.matchRunning()) {

                // Déclenchement du pavillon
                if (nerellRobotStatus.getRemainingTime() <= IConstantesNerellConfig.pavillonRemainingTimeMs && !nerellRobotStatus.pavillon()) {
                    log.info("Activation du pavillon");
                    motorPavillon.speed(motorPavillon.getMaxSpeed() / 2);
                    nerellRobotStatus.pavillon(true);
                }

                ThreadUtils.sleep(200);
            }
            nerellRobotStatus.stopMatch();
            baliseService.idle();
            nerellRobotStatus.disableBalise();
            ioService.disableAlim12VPuissance();
            lidar.stopScan();
            ecranService.displayMessage(String.format("FIN - Durée match %s ms", nerellRobotStatus.getElapsedTime()));

            motorPavillon.speed(motorPavillon.getStopSpeed()); // Ecrit par Nils le 22/03/2020 à 09:47:26
            ioService.disableAlim5VPuissance();

            // On envoi les datas collecté
            ecranService.displayMessage("FIN - Sauvegarde télémétrie");
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
            ecranService.displayMessage(
                    String.format("FIN - Remettre la tirette et AU pour ejection - Score %s",
                            nerellRobotStatus.calculerPoints())
            );

            while (!ioService.tirette() || !ioService.auOk()) {
                ThreadUtils.sleep(1000);
            }

            // Remise en place
            ioService.enableAlim5VPuissance();
            servosService.pincesAvantOuvert(false);
            servosService.pincesArriereOuvert(false);
            servosService.brasDroitFerme(false);
            servosService.brasGaucheFerme(false);
            servosService.ascenseurAvantRoulage(false);
            servosService.ascenseurArriereHaut(false);
            servosService.pivotArriereFerme(false);
            servosService.moustachesFerme(false);
            ecranService.displayMessage("FIN - Attente béquille et enlever la tirette");
            while (ioService.tirette()) {
                ThreadUtils.sleep(1000);
            }
            servosService.pincesAvantFerme(false);
            servosService.pincesArriereFerme(false);
            ThreadUtils.sleep(1000);
        } finally {
            lidar.stopScan();

            // On coupe le jus
            ioService.disableAlim5VPuissance();
            ioService.disableAlim12VPuissance();
        }
    }

    public void positionStrategy() {
        try {
            if (nerellRobotStatus.getStrategy() == EStrategy.AGGRESSIVE) {
                if (nerellRobotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                    trajectoryManager.alignFrontTo(1025, 1400);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                    trajectoryManager.alignFrontTo(3000 - 1025, 1400);
                }
            } else if (nerellRobotStatus.getStrategy() == EStrategy.FINALE) {
                if (nerellRobotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                }
            } else if (nerellRobotStatus.getStrategy() == EStrategy.BASIC_NORD) { // BASIC
                // Aligne vers les bouées au nord du port
                if (nerellRobotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(220, 1290);
                    trajectoryManager.gotoOrientationDeg(66);
                } else {
                    trajectoryManager.gotoPoint(3000 - 220, 1290);
                    trajectoryManager.gotoOrientationDeg(180 - 66);
                }
            } else if (nerellRobotStatus.getStrategy() == EStrategy.BASIC_SUD) {
                // Aligne vers les bouées au sud du port
                if (nerellRobotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(220, 1110);
                    trajectoryManager.gotoOrientationDeg(-66);
                } else {
                    trajectoryManager.gotoPoint(3000 - 220, 1110);
                    trajectoryManager.gotoOrientationDeg(-180 + 66);
                }
            } else { // Au centre orienté vers le logo au centre de la table
                if (nerellRobotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                }
            }

        } catch (AvoidingException e) {
            ecranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
        }
    }

    public void calageBordure(boolean skip) {
        try {
            nerellRobotStatus.disableAvoidance();
            if (nerellRobotStatus.isSimulateur() || skip) {
                if (nerellRobotStatus.getTeam() == ETeam.BLEU) {
                    position.setPt(new Point(conv.mmToPulse(200), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setPt(new Point(conv.mmToPulse(3000 - 200), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(180));
                }
            } else {
                nerellRobotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                if (nerellRobotStatus.getTeam() == ETeam.BLEU) {
                    position.getPt().setX(conv.mmToPulse(IConstantesNerellConfig.dstCallageY));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.getPt().setX(conv.mmToPulse(3000 - IConstantesNerellConfig.dstCallageY));
                    position.setAngle(conv.degToPulse(180));
                }

                trajectoryManager.avanceMM(150);
                trajectoryManager.gotoOrientationDeg(-90);

                nerellRobotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                position.getPt().setY(conv.mmToPulse(2000 - IConstantesNerellConfig.dstCallageY));
                position.setAngle(conv.degToPulse(-90));

                trajectoryManager.avanceMM(150);

                if (nerellRobotStatus.getTeam() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                }
            }
        } catch (AvoidingException e) {
            ecranService.displayMessage("Erreur lors du calage bordure", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer pour le départ", e);
        }
    }

    private void startMonitoring() {
        launchExecId = System.getProperty(IConstantesConfig.keyExecutionId);

        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);
        nerellRobotStatus.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
    }

    @SneakyThrows
    private void endMonitoring() {
        monitoringWrapper.save();
        nerellRobotStatus.disableForceMonitoring();

        final String execId = System.getProperty(IConstantesConfig.keyExecutionId);

        final File execFile = new File("./logs/" + execId + ".exec");
        DateTimeFormatter execIdPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter savePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> lines = new ArrayList<>();
        lines.add(LocalDateTime.parse(execId, execIdPattern).format(savePattern));
        lines.add(LocalDateTime.now().format(savePattern));
        FileUtils.writeLines(execFile, lines);

        log.info("Création du fichier de fin d'éxécution {}", execFile.getAbsolutePath());

        System.setProperty(IConstantesConfig.keyExecutionId, launchExecId);
    }
}
