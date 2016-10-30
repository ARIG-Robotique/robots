package org.arig.robot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.system.MouvementManager;
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

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private IPathFinder pathFinder;

    @Override
    protected void processWithPoints(List<Point> points) {
        if (points.isEmpty()) {
            return;
        }

        // 3.1 Stop du robot
        mouvementManager.setObstacleFound(true);

        List<Polygon> polygons = new ArrayList<>();
        for (Point pt : points) {
            // 3.2 Définition de l'obstacle (autour de nous)
            int r1 = (int) (Math.cos(Math.toRadians(22.5)) * getDistanceCentreObstacle() / 10);
            int r2 = (int) (Math.sin(Math.toRadians(22.5)) * getDistanceCentreObstacle() / 10);

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
        mouvementManager.setRestartAfterObstacle(true);
    }
}