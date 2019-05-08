package org.arig.robot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.CarouselService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.MagasinService;
import org.arig.robot.services.ServosService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
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
    private MagasinService magasinService;

    @Autowired
    private CarouselService carouselService;

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
    @Qualifier("currentPosition")
    private Position position;

    public static Ordonanceur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ordonanceur();
        }

        return INSTANCE;
    }

    public void run() throws RefreshPathFindingException, IOException {
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
            ioService.colorLedRGBKo();
            while(!ioService.auOk()) {
                waitTimeMs(500);
            }
        }

        HealthInfos lidarHealth = lidar.healthInfo();
        if (!lidarHealth.isOk()) {
            log.error("Status du Lidar KO : {} - {} - Code {}", lidarHealth.getState(), lidarHealth.getValue(), lidarHealth.getErrorCode());
            ioService.colorLedRGBKo();
            return;
        }

        ioService.colorLedRGBOk();
        log.info("Arrêt d'urgence OK");

        log.info("Position de préparation des servos moteurs");
        servosService.cyclePreparation();

        // Activation des puissances
        log.info("Activation puissances 5V, 8V et 12V");
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();

        if (!ioService.alimPuissance12VOk() || !ioService.alimPuissance5VOk()) {
            log.warn("Alimentation puissance NOK (12V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk());
            ioService.colorLedRGBKo();
            while(!ioService.alimPuissance12VOk() && !ioService.alimPuissance5VOk()) {
                waitTimeMs(500);
            }
        }
        ioService.colorLedRGBOk();
        log.info("Alimentation puissance OK (12V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk());

        log.info("Démarrage du lidar");
        lidar.startScan();

        log.warn("La tirette n'est pas la et la selection couleur n'as pas eu lieu. Phase de préparation Nerell");
        boolean selectionCouleur = false;
        /*while(!ioService.tirette() || !selectionCouleur) {
            Team selectedTeam = ioService.equipe();
            if (selectedTeam != initTeam && !selectionCouleur && !ioService.tirette()) {
                log.info("Couleur selectionné une première fois");
                selectionCouleur = true;
            }

            if (selectionCouleur) {
                // Affichage de la couleur selectione
                ioService.teamColorLedRGB();
            }

            waitTimeMs(100);
        }*/
        log.info("Phase de préparation terminé");

        log.info("Chargement de la carte");
        String fileResourcePath = String.format("classpath:maps/%s.png", robotStatus.getTeam().name().toLowerCase());
        final InputStream imgMap = patternResolver.getResource(fileResourcePath).getInputStream();
        pathFinder.construitGraphDepuisImageNoirEtBlanc(imgMap);

        // Attente la mise de la tirette
        log.info("Mise de la tirrette pour lancer la calibration");
        while (!ioService.tirette()) {
            waitTimeMs(100);
        }

        // Initialisation Mouvement Manager
        log.info("Initialisation du contrôleur de mouvement");
        trajectoryManager.init();

        robotStatus.disableAvoidance();
        robotStatus.enableAsserv();

        trajectoryManager.setVitesse(IConstantesNerellConfig.vitesseMoyenneBasse, IConstantesNerellConfig.vitesseOrientation);

        if (!robotStatus.isSimulateur()) {
            robotStatus.enableCalageBordure();
            trajectoryManager.reculeMMSansAngle(1000);

            if (robotStatus.getTeam() == Team.JAUNE) {
                position.getPt().setX(conv.mmToPulse(IConstantesNerellConfig.dstDos));
                position.setAngle(conv.degToPulse(0));
            } else {
                position.getPt().setX(conv.mmToPulse(3000 - IConstantesNerellConfig.dstDos));
                position.setAngle(conv.degToPulse(180));
            }

            trajectoryManager.avanceMM(150);
            trajectoryManager.gotoOrientationDeg(-90);

            robotStatus.enableCalageBordure();
            trajectoryManager.reculeMMSansAngle(1000);

            position.getPt().setY(conv.mmToPulse(2000 - IConstantesNerellConfig.dstDos));
            position.setAngle(conv.degToPulse(-90));

            trajectoryManager.avanceMM(150);

            if (robotStatus.getTeam() == Team.JAUNE) {
                trajectoryManager.gotoPointMM(250, 1500);
                trajectoryManager.gotoOrientationDeg(0);
            } else {
                trajectoryManager.gotoPointMM(2750, 1500);
                trajectoryManager.gotoOrientationDeg(180);
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

        log.info("Position initiale avant match des servos");
        //servosService.homes();

        // Attente tirette.
        log.info("!!! ... ATTENTE DEPART TIRRETTE ... !!!");
        while(ioService.tirette()) {
            waitTimeMs(1);
        }

        // Début du compteur de temps pour le match
        robotStatus.startMatch();

        log.info("Démarrage du match");

        // Activation
        robotStatus.enableMatch();
        robotStatus.enableServicesMetier();
//        robotStatus.enableVentouses();
//        robotStatus.enableAvoidance();

        // Match de XX secondes.
//        boolean activateCollecteAdverse = false;
        while(robotStatus.getElapsedTime() < IConstantesNerellConfig.matchTimeMs) {
            waitTimeMs(200);
        }
        robotStatus.stopMatch();
        log.info("Fin de l'ordonancement du match. Durée {} ms", robotStatus.getElapsedTime());

        robotStatus.disableServicesMetier();

        // Désactivation de la puissance moteur pour être sur de ne plus rouler
        ioService.disableAlim5VPuissance();
        ioService.disableAlim12VPuissance();

        // On arrette le lidar
        lidar.stopScan();
        lidar.end();

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

        // Attente remise de la tirette pour ejecter les palets en stock
        while(!ioService.tirette() || !ioService.auOk()) {
            ioService.colorLedRGBOk();
            waitTimeMs(500);
            ioService.clearColorLedRGB();
            waitTimeMs(500);
        }

        // Ejection du stock
        ioService.colorLedRGBKo();
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();

        magasinService.ejectionAvantRetourStand();
        carouselService.ejectionAvantRetourStand();

        ioService.disableAlim5VPuissance();
        ioService.disableAlim12VPuissance();
        ioService.clearColorLedRGB();
    }

    private void waitTimeMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            log.error("Interruption du Thread", e);
        }
    }
}
