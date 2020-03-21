package org.arig.robot.system;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.evaluation.ClusterEvaluator;
import org.arig.robot.model.Cercle;
import org.arig.robot.model.Point;
import org.arig.robot.model.Shape;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class LidarService implements ILidarService, InitializingBean {

    // passe plat pour pas recoder centroidOf(Cluster)
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
    protected TableUtils tableUtils;

    @Autowired
    private IPathFinder pathFinder;

    @Getter
    private final List<Point> detectedPointsMm = Collections.synchronizedList(new ArrayList<>());
    @Getter
    protected final List<Shape> collisionsShape = Collections.synchronizedList(new ArrayList<>());
    @Getter
    protected final AtomicBoolean hasObstacle = new AtomicBoolean(false);

    private final AtomicBoolean cleanup = new AtomicBoolean(false);

    private final DBSCANClusterer<Point> clusterer;
    private final CustomClusterEvaluator<Point> clusterEvaluator = new CustomClusterEvaluator<>();

    final int pathFindingTailleObstacle;
    final int lidarOffsetPointMm;

    public LidarService(int pathFindingTailleObstacle, int lidarOffsetPointMm, int lidarClusterSizeMm) {
        this.pathFindingTailleObstacle = pathFindingTailleObstacle;
        this.lidarOffsetPointMm = lidarOffsetPointMm;

        clusterer = new DBSCANClusterer<>(lidarClusterSizeMm, 2);
    }

    @Override
    synchronized public void waitCleanup() throws InterruptedException {
        cleanup.set(true);

        while (cleanup.get()) {
            wait();
        }
    }

    @Override
    public boolean isConnected() {
        return lidar.isOpen();
    }

    @Override
    public boolean mustCleanup() {
        return cleanup.get();
    }

    @Override
    public void afterPropertiesSet() {
        log.info("Initialisation du service d'évittement d'obstacle");
        lidar.deviceInfo();
    }

    @Override
    public void refreshDetectedPoints() {
        ScanInfos lidarScan = lidar.grabDatas();

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

    @Override
    public void refreshObstacles() {
        refreshObstacles(null);
    }

    @Override
    synchronized public void refreshObstacles(final List<Line2D> lines) {
        List<org.arig.robot.model.Shape> collisionsShape = new ArrayList<>();
        List<java.awt.Shape> obstacles = new ArrayList<>();

        pointLidar:
        for (Point pt : applyClustering(detectedPointsMm)) {
            collisionsShape.add(new Cercle(pt, pathFindingTailleObstacle / 2));
//            collisionsShape.add(new org.arig.robot.model.Rectangle(pt.getX() - pathFindingTailleObstacle / 2., pt.getY() - pathFindingTailleObstacle / 2., pathFindingTailleObstacle, pathFindingTailleObstacle));


            int r1 = (int) (Math.cos(Math.toRadians(22.5)) * pathFindingTailleObstacle / 2 / 10);
            int r2 = (int) (Math.sin(Math.toRadians(22.5)) * pathFindingTailleObstacle / 2 / 10);

            Polygon obstacle = new Polygon();
            obstacle.addPoint(r2, r1);
            obstacle.addPoint(r1, r2);
            obstacle.addPoint(r1, -r2);
            obstacle.addPoint(r2, -r1);
            obstacle.addPoint(-r2, -r1);
            obstacle.addPoint(-r1, -r2);
            obstacle.addPoint(-r1, r2);
            obstacle.addPoint(-r2, r1);
            obstacle.translate((int) pt.getX() / 10, (int) pt.getY() / 10);

//            Rectangle obstacle = new Rectangle(
//                    (int) Math.round(pt.getX() / 10. - pathFindingSeuilProximite / 10. / 2.),
//                    (int) Math.round(pt.getY() / 10. - pathFindingSeuilProximite / 10. / 2.),
//                    (int) Math.round(pathFindingSeuilProximite / 10.),
//                    (int) Math.round(pathFindingSeuilProximite / 10.)
//            );

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

    @Override
    public boolean hasObstacle() {
        return hasObstacle.get();
    }

    List<Point> applyClustering(List<Point> input) {
        return clusterer.cluster(input)
                .stream()
                .map(cluster -> {
                    Clusterable center = clusterEvaluator.getCenter(cluster);
                    return new Point(center.getPoint()[0], center.getPoint()[1]);
                })
                .map(point -> tableUtils.eloigner(point, lidarOffsetPointMm))
                .collect(Collectors.toList());
    }

}
