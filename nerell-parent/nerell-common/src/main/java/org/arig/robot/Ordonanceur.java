package org.arig.robot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.CarouselService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.process.StreamGobbler;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gdepuille on 08/03/15.
 */
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
    private ICarouselManager carouselManager;

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
    private CarouselService carouselService;

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
        // Configuration a faire pour chaque match (gestion sans redemarrage programme)
        // Définition d'un ID unique pour le nommage des fichiers
        final LocalDateTime startOrdonnanceur = LocalDateTime.now();
        final String execId = startOrdonnanceur.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);

        log.info("Demarrage de l'ordonancement du match ...");

        // Equipe au démarrage
        Team initTeam = ioService.equipe();

        try {
            // Bus I2C
            log.info("Scan I2C ...");
            i2CManager.executeScan();
        } catch (I2CException e) {
            log.error("Erreur lors du scan I2C", e);
            return;
        }

        if (!ioService.auOk()) {
            log.warn("L'arrêt d'urgence est coupé.");
            while (!ioService.auOk()) {
                ThreadUtils.sleep(500);
            }
        }

        HealthInfos lidarHealth = lidar.healthInfo();
        if (!lidarHealth.isOk()) {
            log.error("Status du Lidar KO : {} - {} - Code {}", lidarHealth.getState(), lidarHealth.getValue(), lidarHealth.getErrorCode());
            return;
        }

        log.info("Arrêt d'urgence OK");
        log.info("Position de préparation des servos moteurs");
        servosService.cyclePreparation();

        // Activation des puissances
        log.info("Activation puissances 5V, 8V et 12V");
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();

        if (!ioService.alimPuissance12VOk() || !ioService.alimPuissance5VOk()) {
            log.warn("Alimentation puissance NOK (12V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk());
            //ioService.colorLedRGBKo();
            while (!ioService.alimPuissance12VOk() && !ioService.alimPuissance5VOk()) {
                ThreadUtils.sleep(500);
            }
        }
        log.info("Alimentation puissance OK (12V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk());

        List<EStrategy> strategies = ioService.strategies();
        log.info("Equipe : {}", ioService.equipe().name());
        log.info("Stratégies actives : {}", strategies);

        log.info("Chargement de la carte");
        String fileResourcePath = String.format("classpath:maps/%s.png", robotStatus.getTeam().name().toLowerCase());
        final InputStream imgMap = patternResolver.getResource(fileResourcePath).getInputStream();
        pathFinder.construitGraphDepuisImageNoirEtBlanc(imgMap);

        log.info("Définition des zones 'mortes' de la carte.");

        // Exclusion de toutes la zone pente et distributeur personel
        // Inverse de width && height
        tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(0, 0, 3000, 457));

        // Attente la mise de la tirette
        log.info("Mise de la tirette pour lancer la calibration");
        while (!ioService.tirette()) {
            ThreadUtils.sleep(100);
        }

        // Initialisation Mouvement Manager
        log.info("Initialisation du contrôleur de mouvement");
        trajectoryManager.init();

        calageBordure();

        log.info("Démarrage du lidar");
        lidar.startScan();

        log.info("Initialisation du Carousel");
        initialisationCarousel();

        // Attente tirette.
        displayDepart();
        while (ioService.tirette()) {
            ThreadUtils.sleep(1);
        }

        // Début du compteur de temps pour le match
        robotStatus.startMatch();

        // Match de XX secondes.
        while (robotStatus.getElapsedTime() < IConstantesNerellConfig.matchTimeMs) {
            ThreadUtils.sleep(200);
        }

        robotStatus.stopMatch();

        ioService.airElectroVanneDroite();
        ioService.airElectroVanneGauche();
        ioService.disablePompeAVideDroite();
        ioService.disablePompeAVideGauche();
        servosService.pinceSerragePaletGauche(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_REPOS, false);
        servosService.pinceSerragePaletDroit(IConstantesServos.PINCE_SERRAGE_PALET_DROIT_REPOS, false);
        servosService.ejectionMagasinGauche(IConstantesServos.EJECTION_MAGASIN_GAUCHE_OUVERT, false);
        servosService.ejectionMagasinDroit(IConstantesServos.EJECTION_MAGASIN_DROIT_OUVERT, true);

        log.info("Fin de l'ordonancement du match. Durée {} ms", robotStatus.getElapsedTime());

        // Désactivation de la puissance moteur pour être sur de ne plus rouler
        ioService.disableAlim5VPuissance();
        ioService.disableAlim12VPuissance();

        // On arrette le lidar
        lidar.stopScan();

        // On envoi les datas collecté
        monitoringWrapper.save();
        final LocalDateTime stopOrdonnanceur = LocalDateTime.now();
        final File execFile = new File("./logs/" + execId + ".exec");
        DateTimeFormatter savePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> lines = new ArrayList<>();
        lines.add(startOrdonnanceur.format(savePattern));
        lines.add(stopOrdonnanceur.format(savePattern));
        FileUtils.writeLines(execFile, lines);
        log.info("Création du fichier de fin d'éxécution {}", execFile.getAbsolutePath());

        // Visualisation du score
        displayScore();

        // Attente remise de la tirette pour ejecter les palets en stock
        while (!ioService.tirette() || !ioService.auOk()) {
            ThreadUtils.sleep(1000);
        }

        // Ejection du stock
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();

        robotStatus.enableAsservCarousel();
        carouselService.ejectionAvantRetourStand();
        robotStatus.disableAsservCarousel();

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
        } catch (AvoidingException | RefreshPathFindingException e) {
            throw new RuntimeException("Impossible de se placer pour le départ", e);
        }
    }

    private void displayDepart() {
        try {
            ProcessBuilder pb = new ProcessBuilder("figlet", "-f", "big", "\n/!\\ READY /!\\\n");
            Process p = pb.start();

            StreamGobbler out = new StreamGobbler(p.getInputStream(), System.out::println);
            new Thread(out).start();
        } catch (IOException e) {
            log.info("!!! ... ATTENTE DEPART TIRRETTE ... !!!");
        }
    }

    private void displayScore() {
        try {
            ProcessBuilder pb = new ProcessBuilder("figlet", "-f", "big", String.format("\n\n\n\nScore : %d\n", robotStatus.calculerPoints()));
            Process p = pb.start();

            StreamGobbler out = new StreamGobbler(p.getInputStream(), System.out::println);
            StreamGobbler err = new StreamGobbler(p.getErrorStream(), log::error);
            new Thread(out).start();
            new Thread(err).start();
        } catch (IOException e) {
            log.info("Score : {}", robotStatus.calculerPoints());
        }
    }

    public void initialisationCarousel() {
        servosService.porteBarilletGauche(IConstantesServos.PORTE_BARILLET_GAUCHE_OUVERT, false);
        servosService.porteBarilletDroit(IConstantesServos.PORTE_BARILLET_DROIT_OUVERT, false);

        robotStatus.carouselIsNotInitialized();
        robotStatus.disableAsservCarousel();

        carouselManager.rawMotorSpeed(500);
        ThreadUtils.sleep(2000);
        while (!ioService.indexCarousel()) {
            ThreadUtils.sleep(10);
        }
        carouselManager.rawMotorSpeed(-500);
        while (ioService.indexCarousel()) {
            ThreadUtils.sleep(10);
        }
        carouselManager.rawMotorSpeed(400);
        while (!ioService.indexCarousel()) {
            ThreadUtils.sleep(10);
        }
        carouselManager.stop();
        carouselManager.resetEncodeur();

        robotStatus.carouselIsInitialized();
        robotStatus.enableAsservCarousel();

        carouselManager.setVitesse(IConstantesNerellConfig.vitesseCarouselNormal);
        carouselManager.tourne(5 * IConstantesNerellConfig.countPerCarouselIndex + IConstantesNerellConfig.countOffsetInitCarousel);
        carouselManager.waitMouvement();

        servosService.porteBarilletGauche(IConstantesServos.PORTE_BARILLET_GAUCHE_FERME, false);
        servosService.porteBarilletDroit(IConstantesServos.PORTE_BARILLET_DROIT_FERME, true);
    }
}
