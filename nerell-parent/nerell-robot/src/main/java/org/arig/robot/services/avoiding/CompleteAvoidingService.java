package org.arig.robot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.Rectangle;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gdepuille on 07/05/15.
 */
@Slf4j
public class CompleteAvoidingService extends AbstractAvoidingService {

    private static final int DISTANCE_CENTRE_OBSTACLE = 500;

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private IPathFinder pathFinder;

    @Override
    protected void processAvoiding() {
        // Pas de bras, pas de chocolat
        if (CollectionUtils.isEmpty(getDetectedPointsMmCapteurs())
                && CollectionUtils.isEmpty(getDetectedPointsMmLidar())) {
            return;
        }

        // 1 Stop du robot si obstacle trop proche.
        /*boolean proxCapteurs = hasProximiteCapteurs();
        boolean proxLidar = hasProximiteLidar();
        boolean hasProx = proxCapteurs || proxLidar;
        if (hasProx) {
            // Stop, et ensuite on recalcul le path
            log.info("Obstacle a proximité détecté, capteurs : {}, lidar : {}", proxCapteurs, proxLidar);
            trajectoryManager.setObstacleFound(true);
        }*/

        // 2 Detection de collision (ici on est tous en cm)
        Point2D ptFrom = new Point2D.Double(
                conv.pulseToMm(position.getPt().getX()) / 10,
                conv.pulseToMm(position.getPt().getY()) / 10
        );
        Point2D ptTo = new Point2D.Double(
                conv.pulseToMm(cmdRobot.getPosition().getPt().getX()) / 10,
                conv.pulseToMm(cmdRobot.getPosition().getPt().getY()) / 10
        );
        Line2D trajectoryLine = new Line2D.Double(ptFrom, ptTo);

        List<Shape> obstacles = new ArrayList<>();
        List<org.arig.robot.model.Rectangle> colisionShape = new ArrayList<>();
        for (Point pt : getDetectedPointsMmLidar()) {
            // Définition de l'obstacle
            double wh = 2 * DISTANCE_CENTRE_OBSTACLE / 10;
            double x = pt.getX() / 10 - (wh / 2);
            double y = pt.getY() / 10 - (wh / 2);

            Rectangle2D obs = new Rectangle2D.Double(x, y, wh, wh);
            if (obs.intersectsLine(trajectoryLine)) {
                log.info("Collision détectée : {} {}", pt, obs);
                obstacles.add(obs);
                colisionShape.add(new Rectangle(x * 10, y * 10, wh * 10, wh * 10));
            }
        }

        // 3 Une collision est détecté
        if (CollectionUtils.isNotEmpty(obstacles)) {
            pathFinder.addObstacles(obstacles.toArray(new Shape[obstacles.size()]));

            synchronized (this.collisionsShape) {
                this.collisionsShape.clear();
                this.collisionsShape.addAll(colisionShape);
            }

            // On recalcul le path
            trajectoryManager.setCollisionDetected(true);
        } else {

            // Pas de colision
            synchronized (this.collisionsShape) {
                this.collisionsShape.clear();
            }
            pathFinder.addObstacles();
        }
    }
}