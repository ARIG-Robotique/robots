package org.arig.eurobot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.ServosServices;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.capteurs.SRF02I2CSonar;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by gdepuille on 23/12/14.
 */
@Slf4j
@Component
public class TasksScheduler {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private ServosServices servosServices;

    // TODO : A nettoyer ------------------

    @Autowired
    private SD21Servos sd21;

    @Autowired
    @Qualifier("usFront")
    private SRF02I2CSonar usFront;

    @Autowired
    @Qualifier("usGauche")
    private SRF02I2CSonar usGauche;

    @Autowired
    @Qualifier("usDroit")
    private SRF02I2CSonar usDroit;

    @Autowired
    @Qualifier("usBack")
    private SRF02I2CSonar usBack;

    private int positionServosCounter = 0;
    private boolean obstacleDetected = false;

    @Scheduled(fixedDelay = 50)
    public void obstacleAvoidance() {
        if (rs.isMatchEnabled()) {
            switch (positionServosCounter) {
                case 0:
                    sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE);
                    break;
                case 1:
                    sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_GAUCHE);
                    break;
                case 2:
                    sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE);
                    break;
                case 3:
                    sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_DROITE);
                    break;
            }

            // Lecture Sonar
            Future<Integer> frontDistance = usFront.readValue();
            Future<Integer> gaucheDistance = usGauche.readValue();
            Future<Integer> droitDistance = usDroit.readValue();
            Future<Integer> backDistance = usBack.readValue();

            while (!(frontDistance.isDone() && gaucheDistance.isDone() && droitDistance.isDone() && backDistance.isDone()));
            try {
                if (!obstacleDetected && frontDistance.get() <= 25) {
                    log.info("Obstacle detecte");
                    mouvementManager.setObstacleFound(true);
                    obstacleDetected = true;
                } else if (obstacleDetected && frontDistance.get() > 25) {
                    log.info("Obstacle perdu");
                    obstacleDetected = false;
                    mouvementManager.setRestartAfterObstacle(true);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Erreur lors de la récupération des distances : {}", e.toString());
            }
            //positionServosCounter++;
            if (positionServosCounter > 3) {
                positionServosCounter = 0;
            }
        }
    }

    // Fin -------------------------------------------------

    @Scheduled(fixedRate = (long) IConstantesRobot.asservTimeMs)
    public void robotManagerTask() {
        if (rs.isAsservEnabled()) {
            mouvementManager.process();
        } else {
            mouvementManager.stop();
        }
    }

    @Scheduled(fixedDelay = 50)
    public void strategyTask() {
        if (rs.isMatchEnabled()) {
            strategyManager.execute();
        }
    }

    @Scheduled(fixedDelay = 100)
    public void ascenseurTask() {
        if (rs.isMatchEnabled()) {
            servosServices.checkAscenseur();
        }
    }

    @Scheduled(fixedDelay = 100)
    public void produitGaucheTask() {
        if (rs.isMatchEnabled()) {
            servosServices.checkProduitGauche();
        }
    }

    @Scheduled(fixedDelay = 100)
    public void produitDroitTask() {
        if (rs.isMatchEnabled()) {
            servosServices.checkProduitDroit();
        }
    }
}
