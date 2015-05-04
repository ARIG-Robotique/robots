package org.arig.eurobot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.ServosServices;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by gdepuille on 23/12/14.
 */
@Slf4j
@Component
public class Schedulers {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private ServosServices servosServices;

    // TODO : A nettoyer ------------------
    private boolean first = true;

    @Scheduled(fixedDelay = 3000)
    public void trajetTest() throws NoPathFoundException {
        if (rs.isMatchEnabled()) {
            if (first) {
                first = false;
                mouvementManager.gotoPointMM(365, 210);
            }
            mouvementManager.pathTo(900, 1400);
            mouvementManager.pathTo(250, 1200);
            mouvementManager.pathTo(900, 500);
            mouvementManager.pathTo(250, 500);
        }
    }
    // Fin

    @Scheduled(fixedRate = (long) IConstantesRobot.asservTimeMs)
    public void robotManagerTask() {
        if (rs.isAsservEnabled()) {
            mouvementManager.process();
        } else {
            mouvementManager.stop();
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

    @Scheduled(fixedDelay = 10)
    public void obstacleAvoidance() {
        servosServices.nextSonarPosition();
    }
}
