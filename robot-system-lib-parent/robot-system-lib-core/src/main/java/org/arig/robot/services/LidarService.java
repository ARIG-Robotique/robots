package org.arig.robot.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.evaluation.ClusterEvaluator;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.Cercle;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.Shape;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.pathfinding.PathFinder;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Fonctions de haut niveau utilisant le Lidar : nettoyage des points et détection d'obsctacles
 */
@Slf4j
@Service
public class LidarService implements InitializingBean {

    // passe plat pour pas recoder centroidOf(Cluster) qui n'est pas publique
    private static class CustomClusterEvaluator<T extends Clusterable> extends ClusterEvaluator<T> {
        public Clusterable getCenter(Cluster<T> cluster) {
            return super.centroidOf(cluster);
        }

        @Override
        public double score(List<? extends Cluster<T>> clusters) {
            return 0;
        }
    }

    @Autowired
    private ILidarTelemeter lidar;

    @Autowired
    private TableUtils tableUtils;

    @Autowired
    private RobotConfig robotConfig;

    @Autowired
    private AbstractRobotStatus robotStatus;

    @Autowired
    private PathFinder pathFinder;

    @Getter
    private final List<Point> detectedPointsMm = Collections.synchronizedList(new ArrayList<>());
    @Getter
    protected final List<Shape> collisionsShape = Collections.synchronizedList(new ArrayList<>());
    @Getter
    protected final AtomicBoolean hasObstacle = new AtomicBoolean(false);

    private final AtomicBoolean cleanup = new AtomicBoolean(false);

    private DBSCANClusterer<Point> clusterer;
    private CustomClusterEvaluator<Point> clusterEvaluator = new CustomClusterEvaluator<>();

    synchronized public boolean waitCleanup() {
        cleanup.set(true);

        while (cleanup.get()) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                log.error("Attente de cleanup interrompu", e);
            }
        }

        return !cleanup.get();
    }

    public boolean isConnected() {
        return lidar.isOpen();
    }

    public boolean mustCleanup() {
        return cleanup.get();
    }

    public void afterPropertiesSet() {
        log.info("Initialisation du service d'évittement d'obstacle");

        clusterEvaluator = new CustomClusterEvaluator<>();
        clusterer = new DBSCANClusterer<>(robotConfig.lidarClusterSizeMm(), 2);

        lidar.deviceInfo();
    }

    public void refreshDetectedPoints() {
        ScanInfos lidarScan = lidar.grabData();

        List<Point> detectedPointsMm = new ArrayList<>();

        if (lidarScan != null) {
            detectedPointsMm.addAll(
                    lidarScan.getScan().parallelStream()
                            .map(scan -> tableUtils.getPointFromAngle(scan.getDistanceMm(), scan.getAngleDeg()))
                            .filter(pt -> tableUtils.isInTable(pt) /*&& checkValidPointForSeuil(pt, IConstantesNerellConfig.pathFindingSeuilAvoidance)*/)
                            .collect(Collectors.toList())
            );
        }

        this.detectedPointsMm.clear();
        this.detectedPointsMm.addAll(detectedPointsMm);
    }

    public void refreshObstacles() {
        refreshObstacles(null);
    }

    public boolean hasObstacleInZone(Rectangle zone) {
        if (zone == null) {
            return false;
        }
        return detectedPointsMm.stream()
                .anyMatch(s -> zone.contains(s.getX(), s.getY()));
    }

    synchronized public void refreshObstacles(final List<Line2D> lines) {
        List<org.arig.robot.model.Shape> collisionsShape = new ArrayList<>();
        List<java.awt.Shape> obstacles = new ArrayList<>();

        pointLidar:
        for (Point pt : applyClustering(detectedPointsMm)) {
            int tailleObstacle = isOtherRobot(pt) ? robotConfig.pathFindingTailleObstacleArig() : robotConfig.pathFindingTailleObstacle();

            collisionsShape.add(new Cercle(pt, tailleObstacle / 2));
//            collisionsShape.add(new org.arig.robot.model.Rectangle(pt.getX() - tailleObstacle / 2., pt.getY() - tailleObstacle / 2., tailleObstacle, tailleObstacle));

            Polygon obstacle = tableUtils.createPolygonObstacle(pt, tailleObstacle);

            if (CollectionUtils.isEmpty(lines)) {
                log.info("Ajout polygon : {} {}", pt, obstacle.toString());
                obstacles.add(obstacle);
            } else {
                for (Line2D l : lines) {
                    if (l.intersects(obstacle.getBounds())) {
                        log.info("Collision détectée, ajout polygon : {} {}", pt, obstacle.toString());
                        obstacles.add(obstacle);
                        continue pointLidar;
                    }
                }
            }
        }

        this.collisionsShape.clear();
        this.collisionsShape.addAll(collisionsShape);

        pathFinder.setObstacles(obstacles);

        hasObstacle.set(!obstacles.isEmpty());

        cleanup.set(false);
        notify();
    }

    public boolean hasObstacle() {
        return hasObstacle.get();
    }

    private List<Point> applyClustering(List<Point> input) {
        return clusterer.cluster(input)
                .stream()
                .map(cluster -> {
                    Clusterable center = clusterEvaluator.getCenter(cluster);
                    return new Point(center.getPoint()[0], center.getPoint()[1]);
                })
                .map(point -> tableUtils.eloigner(point, robotConfig.lidarOffsetPointMm()))
                .collect(Collectors.toList());
    }

    public boolean isOtherRobot(Point pt) {
        return robotStatus.otherPosition() != null && pt.distance(robotStatus.otherPosition()) < robotConfig.pathFindingSeuilProximiteArig() / 3.0;
    }

}
