package org.arig.robot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.system.TrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * @author gdepuille on 07/05/15.
 */
@Slf4j
public class BasicAvoidingService extends AbstractAvoidingService {

    private static final int SEUIL_DISTANCE_MM = 200;

    @Autowired
    private TrajectoryManager trajectoryManager;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    private boolean currentObstacle = false;

    @Override
    protected void processAvoiding() {
        boolean hasObstacle = getDetectedPoints().parallelStream().
            anyMatch(pt -> {
                long dX = (long) (pt.getX() * 10 - conv.pulseToMm(position.getPt().getX()));
                long dY = (long) (pt.getY() * 10 - conv.pulseToMm(position.getPt().getY()));
                double distanceMm = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
                if (distanceMm > SEUIL_DISTANCE_MM) {
                    return false;
                }

                double alpha = Math.toDegrees(Math.atan2(Math.toRadians(dY), Math.toRadians(dX)));
                double dA = alpha - conv.pulseToDeg(position.getAngle());
                if (dA > 180) {
                    dA -= 360;
                } else if (dA < -180) {
                    dA += 360;
                }

                return dA > -45 && dA < 45;
            });

        // Log de détection avec un sémaphore
        if (hasObstacle && !currentObstacle) {
            log.info("Attente que l'obstacle sans aille ...");
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
