package org.arig.robot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.Point;
import org.arig.robot.system.TrajectoryManager;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gdepuille on 07/05/15.
 */
@Slf4j
public class CompleteAvoidingService extends AbstractAvoidingService {

    private static final int DISTANCE_CENTRE_OBSTACLE = 500;

    @Autowired
    private TrajectoryManager trajectoryManager;

    @Autowired
    private IPathFinder pathFinder;

    @Override
    protected void processAvoiding() {
        if (CollectionUtils.isEmpty(getDetectedPointsMmCapteurs())) {
            return;
        }

        // 3.1 Stop du robot
        //trajectoryManager.setObstacleFound(true);

        List<Polygon> polygons = new ArrayList<>();
        for (Point pt : getDetectedPointsMmCapteurs()) {
            // 3.2 Définition de l'obstacle (autour de nous)
            int r1 = (int) (Math.cos(Math.toRadians(22.5)) * DISTANCE_CENTRE_OBSTACLE / 10);
            int r2 = (int) (Math.sin(Math.toRadians(22.5)) * DISTANCE_CENTRE_OBSTACLE / 10);

            Polygon polygonObstacle = new Polygon();
            polygonObstacle.addPoint(r2, r1);
            polygonObstacle.addPoint(r1, r2);
            polygonObstacle.addPoint(r1, -r2);
            polygonObstacle.addPoint(r2, -r1);
            polygonObstacle.addPoint(-r2, -r1);
            polygonObstacle.addPoint(-r1, -r2);
            polygonObstacle.addPoint(-r1, r2);
            polygonObstacle.addPoint(-r2, r1);
            polygonObstacle.translate((int) pt.getX() / 10, (int) pt.getY() / 10);
            polygons.add(polygonObstacle);
        }
        // 3.3 Mise à jour de la map du path finding
        pathFinder.addObstacles(polygons.toArray(new Polygon[polygons.size()]));

        // 3.4 On relance le bouzin
        trajectoryManager.setRestartAfterObstacle(true);
    }
}