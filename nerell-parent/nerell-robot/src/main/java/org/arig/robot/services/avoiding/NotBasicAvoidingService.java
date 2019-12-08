package org.arig.robot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.TypeConsigne;

/**
 * Evitement sans path-finding : arret complet en cas d'obstacle et reprise après 1 seconde de blocage
 */
@Slf4j
public class NotBasicAvoidingService extends AbstractAvoidingService {

    private boolean currentObstacle = false;

    private int obstacleCount = 0;

    @Override
    protected void processAvoiding() {
        if (!rs.isAvoidanceEnabled()) {
            return;
        }

        boolean hasObstacle = hasProximite();

        // Log de détection avec un sémaphore
        if (hasObstacle && !currentObstacle) {
            log.info("Attente que l'obstacle sans aille ...");
            currentObstacle = true;
            obstacleCount = 0;
        }
        if (hasObstacle) {
            obstacleCount++;
            trajectoryManager.obstacleFound();

            if (obstacleCount > 10) { // le scheduler est a 100ms => 1s d'attente
                log.warn("L'obstacle n'est pas parti après 1sec, on annule tout");

                cmdRobot.getConsigne().setDistance(0);
                cmdRobot.getConsigne().setOrientation(0);
                cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);

                trajectoryManager.obstacleNotFound();
                trajectoryManager.cancelMouvement();

                rs.disableAvoidance();

                currentObstacle = false;
            }
        }

        if (!hasObstacle && currentObstacle) {
            log.info("L'obstacle à disparu on relance le cycle.");
        }

        if (!hasObstacle) {
            // 3.4 On relance le bouzin
            trajectoryManager.obstacleNotFound();
            currentObstacle = false;
            obstacleCount = 0;
        }
    }
}
