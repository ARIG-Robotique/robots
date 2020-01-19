package org.arig.robot.utils;

import org.arig.robot.model.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AvoidingUtils {

    private static final int SEUIL_CLUSTERING = 100; // 10 cm

    public static List<Point> calculateCenterObs(List<Point> detectedPointsMm) {
        List<List<Point>> clusters = clusterObs(detectedPointsMm);
        return calculateCentroidObs(clusters);
    }

    private static List<Point> calculateCentroidObs(List<List<Point>> clusters) {
        return clusters.stream()
                .map(AvoidingUtils::centroid)
                .collect(Collectors.toList());
    }

    private static Point centroid(List<Point> points) {
        double x = 0;
        double y = 0;

        for (int i = 0; i < points.size(); i++) {
            x += points.get(i).getX();
            y += points.get(i).getY();
        }

        // TODO: idée d'améliorer, le centroid peut etre décalé par rapport à cette valeur parce que le lidar ne voit que le front de l'obstacle

        return new Point(x / points.size(), y / points.size());
    }

    private static List<List<Point>> clusterObs(List<Point> detectedPointsMm) {
        final Point originePoint = new Point(0, 0);

        List<Point> sortedPoints = detectedPointsMm.stream()
                .sorted((p1, p2) -> {
                    Double p1Dist = p1.distance(originePoint);
                    Double p2Dist = p2.distance(originePoint);
                    return p1Dist.compareTo(p2Dist);
                })
                .collect(Collectors.toList());

        List<List<Point>> cluster = new ArrayList<>();

        int currentClusterPos = 0;

        clustering:
        for (int i = 0; i < sortedPoints.size(); i++) {
            Point point = sortedPoints.get(i);

            List<Point> currentCluster;

            if (currentClusterPos >= cluster.size()) {
                currentCluster = new ArrayList<>();
                currentCluster.add(point);
                cluster.add(currentCluster);
                continue clustering;
            } else {
                currentCluster = cluster.get(currentClusterPos);
            }

            if (point.distance(currentCluster.get(currentCluster.size() - 1)) < SEUIL_CLUSTERING ||
                    point.distance(currentCluster.get(0)) < SEUIL_CLUSTERING) {
                currentCluster.add(point);
            } else {
                currentClusterPos = currentClusterPos + 1;
                currentCluster = new ArrayList<>();
                currentCluster.add(point);
                cluster.add(currentCluster);
            }
        }

        return cluster;
    }
}
