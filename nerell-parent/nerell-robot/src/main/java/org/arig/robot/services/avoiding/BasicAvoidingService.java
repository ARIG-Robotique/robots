package org.arig.robot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.TrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gdepuille on 07/05/15.
 */
@Slf4j
public class BasicAvoidingService extends AbstractAvoidingService {

    @Autowired
    private TrajectoryManager trajectoryManager;

    private boolean currentObstacle = false;

    @Override
    protected void processAvoiding() {
        boolean proxCapteurs = hasProximiteCapteurs();
        boolean proxLidar = hasProximiteLidar();
        boolean hasObstacle = proxCapteurs || proxLidar;

        // Log de détection avec un sémaphore
        if (hasObstacle && !currentObstacle) {
            log.info("Attente que l'obstacle sans aille (lidar {} ; capteurs {}) ...", proxLidar, proxCapteurs);
            currentObstacle = true;
        }
        if (hasObstacle) {
            trajectoryManager.setObstacleFound(true);
        }

        if (!hasObstacle && currentObstacle) {
            log.info("L'obstacle à disparu on relance le cycle.");
        }

        if (!hasObstacle) {
            // 3.4 On relance le bouzin
            trajectoryManager.setObstacleFound(false);
            currentObstacle = false;
        }
    }
}
