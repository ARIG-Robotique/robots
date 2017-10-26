package org.arig.robot.system.avoiding;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.Cercle;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.Shape;
import org.arig.robot.model.enums.TypeMouvement;
import org.arig.robot.model.monitor.AbstractMonitorMouvement;
import org.arig.robot.model.monitor.MonitorMouvementPath;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gdepuille on 22/05/17.
 */
@Slf4j
public class AvoidingServiceBouchon implements IAvoidingService, InitializingBean {

    private static final int DISTANCE_CENTRE_OBSTACLE = 250;

    private static final int DISTANCE_FILTRAGE_OBSTACLES = 300;

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition;

    @Autowired
    private IPathFinder pathFinder;

    @Getter
    private final List<Point> detectedPointsMmCapteurs = Collections.emptyList();
    @Getter
    private final List<Point> detectedPointsMmLidar = new ArrayList<>();
    @Getter
    protected final List<Shape> collisionsShape = new ArrayList<>();

    List<java.awt.Shape> tmpObstacles = new ArrayList<>();
    List<org.arig.robot.model.Shape> tmpCollisionsShape = new ArrayList<>();
    private List<Line2D> lines = new ArrayList<>(); // Chemin parcouru
    private AbstractMonitorMouvement currentMvt; // Mouvement courrant
    private double checkSumLidar = 0; // Checksum des coordonnées lidar pour detecter les changements
    private boolean hasObstacle = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            double y = 1000;
            int inc = 1;
            while (true) {
                if (!detectedPointsMmLidar.isEmpty()) {
                    if (inc > 0 && y >= 1000) {
                        inc = -1;
                    } else if (inc < 0 && y <= 300) {
                        inc = 1;
                    }
                    y = detectedPointsMmLidar.get(0).getY() + inc;
                }

                detectedPointsMmLidar.clear();
                detectedPointsMmLidar.add(new Point(1500, y));
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // NOP
                }
            }

        }).start();
    }

    @Override
    public void process() {
        checkMouvement();
        checkObstacles();

        // Une collision est détecté
        if (CollectionUtils.isNotEmpty(tmpObstacles)) {
            hasObstacle = true;
            pathFinder.addObstacles(tmpObstacles.toArray(new java.awt.Shape[tmpObstacles.size()]));

            synchronized (this.collisionsShape) {
                this.collisionsShape.clear();
                this.collisionsShape.addAll(tmpCollisionsShape);
            }

            // On rafraichit le path
            trajectoryManager.refreshPathFinding();
        }

        if (hasObstacle && CollectionUtils.isEmpty(tmpObstacles)) {
            clearOstacles();
        }
    }

    private boolean checkMouvement() {
        boolean mouvementHasChanged = false;

        // Construction du chemin a parcourir sur le changement de mouvement
        if (currentMvt != trajectoryManager.getCurrentMouvement()) {
            log.info("Le mouvement courrant a changé");
            mouvementHasChanged = true;
            currentMvt = trajectoryManager.getCurrentMouvement();

            lines.clear();
            if (currentMvt.getType() == TypeMouvement.PATH) {
                MonitorMouvementPath mp = (MonitorMouvementPath) currentMvt;

                Point2D ptFrom = new Point2D.Double(
                        conv.pulseToMm(currentPosition.getPt().getX()) / 10,
                        conv.pulseToMm(currentPosition.getPt().getY()) / 10
                );
                Point2D ptTo;
                for (Point pt : mp.getPath()) {
                    ptTo = new Point2D.Double(
                            pt.getX() / 10,
                            pt.getY() / 10
                    );

                    lines.add(new Line2D.Double(ptFrom, ptTo));
                    ptFrom = new Point2D.Double(ptTo.getX(), ptTo.getY());
                }
            }
        }

        return mouvementHasChanged;
    }

    private boolean checkObstacles() {
//        boolean obstaclesHasChanged = false;

        // Si on a un trajet, alors on check
        // Si les points lidar on changé on check également
//        double checkSumLidar = getDetectedPointsMmLidar().parallelStream()
//                .mapToDouble(p -> p.getX() + p.getY())
//                .sum();

//        if (checkSumLidar != this.checkSumLidar) {
//            this.checkSumLidar = checkSumLidar;
//            obstaclesHasChanged = true;

            tmpObstacles.clear();
            tmpCollisionsShape.clear();

            pointLidar : for (Point pt : getDetectedPointsMmLidar()) {

                // Si l'angle absolu du point est supérieur à 100°, le point est dérrière nous.
                // Comme on va principalement en avant on exclu.
                double dX = conv.mmToPulse(pt.getX()) - currentPosition.getPt().getX();
                double dY = conv.mmToPulse(pt.getY()) - currentPosition.getPt().getY();
                double angle = Math.toDegrees(Math.atan2(conv.pulseToRad(dY), conv.pulseToRad(dX)));
                if (Math.abs(angle) >= 100) {
                    continue;
                }

                // Définition de l'obstacle polygone (autour de nous)
                int r1 = (int) (Math.cos(Math.toRadians(22.5)) * DISTANCE_CENTRE_OBSTACLE / 10);
                int r2 = (int) (Math.sin(Math.toRadians(22.5)) * DISTANCE_CENTRE_OBSTACLE / 10);

                Polygon obsPoly = new Polygon();
                obsPoly.addPoint(r2, r1);
                obsPoly.addPoint(r1, r2);
                obsPoly.addPoint(r1, -r2);
                obsPoly.addPoint(r2, -r1);
                obsPoly.addPoint(-r2, -r1);
                obsPoly.addPoint(-r1, -r2);
                obsPoly.addPoint(-r1, r2);
                obsPoly.addPoint(-r2, r1);
                obsPoly.translate((int) pt.getX() / 10, (int) pt.getY() / 10);

                for (Line2D l : lines) {
                    if (l.intersects(obsPoly.getBounds())) {
                        log.info("Collision détectée, ajout polygon : {} {}", pt, obsPoly);
                        tmpObstacles.add(obsPoly);
                        tmpCollisionsShape.add(new Cercle(pt, DISTANCE_CENTRE_OBSTACLE));
                        continue pointLidar;
                    }
                }
            }
//        }

//        return obstaclesHasChanged;
        return true;
    }

    private void clearOstacles() {
        log.info("Nettoyage des obstacles");
        hasObstacle = false;
        pathFinder.addObstacles();

        synchronized (this.collisionsShape) {
            this.collisionsShape.clear();
        }

        // On rafraichit le path
        trajectoryManager.refreshPathFinding();
    }
}
