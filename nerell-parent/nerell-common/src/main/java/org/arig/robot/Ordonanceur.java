package org.arig.robot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.EjectionModuleService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;

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
    private EjectionModuleService ejectionModuleService;

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
    @Qualifier("currentPosition")
    private Position position;

    public static Ordonanceur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ordonanceur();
        }

        return INSTANCE;
    }

    public void run() throws RefreshPathFindingException, IOException {
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

        log.info("Position initiale de l'ejection module");
        ejectionModuleService.init();

        log.warn("La tirette n'est pas la et la selection couleur n'as pas eu lieu. Phase de préparation Nerell");
        boolean selectionCouleur = false;
        while(!ioService.tirette() || !selectionCouleur) {
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
        }
        log.info("Phase de préparation terminé");

        log.info("Chargement de la carte");
        String fileResourcePath = String.format("classpath:maps/%s.png", robotStatus.getTeam().name().toLowerCase());
        final InputStream imgMap = patternResolver.getResource(fileResourcePath).getInputStream();
        pathFinder.construitGraphDepuisImageNoirEtBlanc(imgMap);

        // Initialisation Mouvement Manager
        log.info("Initialisation du contrôleur de mouvement");
        trajectoryManager.init();

        robotStatus.disableAvoidance();
        robotStatus.enableAsserv();

        trajectoryManager.setVitesse(IConstantesNerellConfig.vitesseSuperLente, IConstantesNerellConfig.vitesseOrientation);
        position.setAngle(conv.degToPulse(90));

        if (!robotStatus.isSimulateur()) {
            if (robotStatus.getTeam() == Team.JAUNE) {
                position.setPt(new Point(conv.mmToPulse(320), conv.mmToPulse(772)));
                trajectoryManager.avanceMM(200);
                trajectoryManager.gotoPointMM(1100, 772);
                trajectoryManager.gotoPointMM(890, 300);
                trajectoryManager.gotoOrientationDeg(90, SensRotation.TRIGO);
                trajectoryManager.reculeMM(135);
                servosService.aspirationFerme();
            } else {
                position.setPt(new Point(conv.mmToPulse(2680), conv.mmToPulse(772)));
                trajectoryManager.avanceMM(300);
                servosService.aspirationFerme();
                trajectoryManager.gotoPointMM(3000 - 1100, 772);
                trajectoryManager.gotoPointMM(3000 - 890, 300);
                trajectoryManager.gotoOrientationDeg(90, SensRotation.TRIGO);
                trajectoryManager.reculeMM(135);
            }

        } else {
            if (robotStatus.getTeam() == Team.JAUNE) {
                position.setPt(new Point(conv.mmToPulse(890), conv.mmToPulse(165)));
            } else {
                position.setPt(new Point(conv.mmToPulse(3000 - 890), conv.mmToPulse(165)));
            }
        }

        log.info("Position initiale avant match des servos");
        servosService.homes();
        robotStatus.enablePinces();

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
        robotStatus.enableAvoidance();

        // Match de XX secondes.
        boolean activateCollecteAdverse = false;
        while(robotStatus.getElapsedTime() < IConstantesNerellConfig.matchTimeMs) {
            if (robotStatus.getElapsedTime() > 45000 && !activateCollecteAdverse) {
                activateCollecteAdverse = true;
                log.info("Activation par le temps de la collecte dans la zone adverse");
                System.setProperty("strategy.collect.zone.adverse", "true");
            }
            waitTimeMs(200);
        }
        robotStatus.stopMatch();
        log.info("Fin de l'ordonancement du match. Durée {} ms", robotStatus.getElapsedTime());

        // Arrêt de l'asservissement et des moteurs
        robotStatus.disableAsserv();
        robotStatus.disableAvoidance();
        robotStatus.disableMatch();

        // Désactivation de la puissance moteur pour être sur de ne plus rouler
        ioService.disableAlim5VPuissance();
        ioService.disableAlim12VPuissance();

        // On arrette le lidar
        lidar.stopScan();
        lidar.end();

        // On envoi les datas collecté
        monitoringWrapper.save();

        // TODO : Attente remise de la tirette pour ejecter les modules et les balles en stocks
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

        ejectionModuleService.ejectionAvantRetourStand();

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
