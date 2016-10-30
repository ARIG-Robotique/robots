package org.arig.robot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author gdepuille on 07/05/15.
 */
@Slf4j
public class BasicAvoidingService extends AbstractAvoidingService {

    @Autowired
    private MouvementManager mouvementManager;

    private boolean currentObstacle = false;

    @Override
    protected void processWithPoints(List<Point> points) {
        // Log de détection avec un sémaphore
        if (!points.isEmpty() && !currentObstacle) {
            log.info("Attente que l'obstacle sans aille ...");
            currentObstacle = true;
        }
        if (!points.isEmpty()) {
            mouvementManager.setObstacleFound(true);
        }

        if (points.isEmpty() && currentObstacle) {
            log.info("L'obstacle à disparu on relance le cycle.");
        }

        if (points.isEmpty()) {
            // 3.4 On relance le bouzin
            mouvementManager.setObstacleFound(false);
            currentObstacle = false;
        }
    }
}
