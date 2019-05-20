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
import java.util.List;

@Slf4j
public class AvoidingServiceBouchon implements IAvoidingService, InitializingBean {

    private static final int DISTANCE_CENTRE_OBSTACLE = 400;

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private IPathFinder pathFinder;

    @Getter
    private final List<Point> detectedPointsMm = new ArrayList<>();
    @Getter
    protected final List<Shape> collisionsShape = new ArrayList<>();

    private boolean hasObstacle = false;

    private AbstractMonitorMouvement currentMvt;

    private int checkSumLidar = 0; // Checksum des coordonnées lidar pour detecter les changements

    private List<Line2D> lines = new ArrayList<>(); // Chemin parcouru

    private List<java.awt.Shape> tmpObstacles = new ArrayList<>(); // Obstacles pour le pathfinding

    private List<org.arig.robot.model.Shape> tmpCollisionsShape = new ArrayList<>(); // Obstacles pour le superviseur

    @Override
    public void afterPropertiesSet() {
//        new Thread(() -> {
//            double y = 1000, y2 = 300, y3 = 1800;
//            int inc = 1, inc2 = 2, inc3 = 3;
//            while (true) {
//                if (!detectedPointsMm.isEmpty()) {
//                    if (inc > 0 && y >= 1800) {
//                        inc = -1;
//                    } else if (inc < 0 && y <= 200) {
//                        inc = 1;
//                    }
//                    y = detectedPointsMm.get(0).getY() + inc;
//
//                    if (inc2 > 0 && y2 >= 1800) {
//                        inc2 = -2;
//                    } else if (inc2 < 0 && y2 <= 200) {
//                        inc2 = 2;
//                    }
//                    y2 = detectedPointsMm.get(1).getY() + inc2;
//
//                    if (inc3 > 0 && y3 >= 1800) {
//                        inc3 = -3;
//                    } else if (inc3 < 0 && y3 <= 200) {
//                        inc3 = 3;
//                    }
//                    y3 = detectedPointsMm.get(2).getY() + inc3;
//                }
//
//
//                synchronized (this.detectedPointsMm) {
//                    detectedPointsMm.clear();
//                    detectedPointsMm.add(new Point(1500, y));
//                    detectedPointsMm.add(new Point(700, y2));
//                    detectedPointsMm.add(new Point(2300, y3));
//                }
//
//                ThreadUtils.sleep(20);
//            }
//        }).start();
    }

    @Override
    public void process() {
        processAvoiding();
    }

    private void processAvoiding() {
        checkMouvement();
        checkObstacles();

        // Affichage des zones d'ombre sur le superviseur
        synchronized (this.collisionsShape) {
            this.collisionsShape.clear();
            this.collisionsShape.addAll(tmpCollisionsShape);
        }

        // Une collision est détecté
        if (CollectionUtils.isNotEmpty(tmpObstacles)) {
            setObstacles(tmpObstacles);

        } else if (hasObstacle) {
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
                        conv.pulseToMm(position.getPt().getX()) / 10,
                        conv.pulseToMm(position.getPt().getY()) / 10
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

        // Si les points lidar on changé on check
//        int checkSumLidar = getDetectedPointsMm().hashCode();
//
//        if (checkSumLidar != this.checkSumLidar) {
//            this.checkSumLidar = checkSumLidar;
//            obstaclesHasChanged = true;

        tmpObstacles.clear();
        tmpCollisionsShape.clear();

        pointLidar:
        for (Point pt : getDetectedPointsMm()) {
            tmpCollisionsShape.add(new Cercle(pt, DISTANCE_CENTRE_OBSTACLE));

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
                    log.info("Collision détectée, ajout polygon : {} {}", pt, obsPoly.getBounds2D());
                    tmpObstacles.add(obsPoly);
                    continue pointLidar;
                }
            }
        }
//        }

//        return obstaclesHasChanged;
        return true;
    }

    private void setObstacles(List<java.awt.Shape> ostacles) {
        hasObstacle = true;
        pathFinder.setObstacles(ostacles.toArray(new java.awt.Shape[0]));

        // On rafraichit le path
        trajectoryManager.refreshPathFinding();
    }

    private void clearOstacles() {
        hasObstacle = false;
        pathFinder.setObstacles();

        // On rafraichit le path
        trajectoryManager.refreshPathFinding();
    }
}
