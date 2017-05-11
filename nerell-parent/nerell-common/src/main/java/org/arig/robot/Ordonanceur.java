package org.arig.robot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.system.TrajectoryManager;
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
    private ServosService servosService;

    @Autowired
    private TrajectoryManager trajectoryManager;

    @Autowired
    private IPathFinder pathFinder;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Autowired
    private ILidarTelemeter rplidar;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    public static Ordonanceur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ordonanceur();
        }

        return INSTANCE;
    }

    public void run() throws ObstacleFoundException, IOException {
        log.info("Demarrage de l'ordonancement du match ...");

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
            while(!ioService.auOk());
        }
        ioService.colorLedRGBOk();
        log.info("Arrêt d'urgence OK");

        log.info("Position initiale des servos moteurs");
        servosService.homes();

        // Activation des puissances
        log.info("Activation puissances 5V, 8V et 12V");
        ioService.enableAlim5VPuissance();
        ioService.enableAlim8VPuissance();
        ioService.enableAlim12VPuissance();

        if (!ioService.alimPuissance12VOk() || !ioService.alimPuissance8VOk() || !ioService.alimPuissance5VOk()) {
            log.warn("Alimentation puissance NOK (12V : {} ; 8V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance8VOk(), ioService.alimPuissance5VOk());
            ioService.colorLedRGBKo();
            while(!ioService.alimPuissance12VOk() && !ioService.alimPuissance8VOk() && !ioService.alimPuissance5VOk());
        }
        ioService.colorLedRGBOk();
        log.info("Alimentation puissance OK (12V : {} ; 8V : {} ; 5V : {})", ioService.alimPuissance12VOk(), ioService.alimPuissance5VOk(), ioService.alimPuissance5VOk());

        if (!ioService.tirette()) {
            log.warn("La tirette n'est pas la. Phase de préparation Nerell");
            while(!ioService.tirette()) {
                ioService.equipe();
                ioService.teamColorLedRGB();
            }
        }
        log.info("Phase de préparation terminé");

        log.info("Chargement de la carte");
        final InputStream imgMap = patternResolver.getResource("classpath:maps/autres/table-test.png").getInputStream();
        pathFinder.construitGraphDepuisImageNoirEtBlanc(imgMap);

        // Initialisation Mouvement Manager
        log.info("Initialisation du contrôleur de mouvement");
        trajectoryManager.init();

        robotStatus.disableAvoidance();
        robotStatus.enableAsserv();

        trajectoryManager.setVitesse(IConstantesNerellConfig.vitesseSuperLente, IConstantesNerellConfig.vitesseSuperLente);
        position.setPt(new Point(conv.mmToPulse(165), conv.mmToPulse(165)));
        position.setAngle(conv.degToPulse(90));

        trajectoryManager.gotoPointMM(590, 300);
        trajectoryManager.gotoOrientationDeg(90);

        // Attente tirette.
        log.info("!!! ... ATTENTE DEPART TIRRETTE ... !!!");
        while(ioService.tirette());

        // Début du compteur de temps pour le match
        robotStatus.startMatch();

        log.info("Démarrage du match");

        // Activation
        robotStatus.enableMatch();
        robotStatus.enableAvoidance();

        // Match de XX secondes.
        boolean activateCollecteAdverse = false;
        while(robotStatus.getElapsedTime() < IConstantesNerellConfig.matchTimeMs) {
            try {
                if (robotStatus.getElapsedTime() > 45000 && !activateCollecteAdverse) {
                    activateCollecteAdverse = true;
                    log.info("Activation par le temps de la collecte dans la zone adverse");
                    System.setProperty("strategy.collect.zone.adverse", "true");
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.error("Interruption du Thread", e);
            }
        }
        robotStatus.stopMatch();
        log.info("Fin de l'ordonancement du match. Durée {} ms", robotStatus.getElapsedTime());

        // Arrêt de l'asservissement et des moteurs
        robotStatus.disableAsserv();
        robotStatus.disableAvoidance();
        robotStatus.disableMatch();

        // Désactivation de la puissance moteur pour être sur de ne plus rouler
        ioService.disableAlim5VPuissance();
        ioService.disableAlim8VPuissance();
        ioService.disableAlim12VPuissance();

        // On éteint la couleur de la team.
        ioService.colorLedRGBOk();

        // On arrette le lidar
        rplidar.stopScan();
        rplidar.end();

        // On envoi les datas collecté
        monitoringWrapper.save();

        // TODO : Attente remise de la tirette pour ejecter les modules et les balles en stocks

    }
}
