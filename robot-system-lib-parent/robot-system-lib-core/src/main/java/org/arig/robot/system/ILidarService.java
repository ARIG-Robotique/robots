package org.arig.robot.system;

import org.arig.robot.model.Point;
import org.arig.robot.model.Shape;

import java.awt.geom.Line2D;
import java.util.List;


/**
 * Fonctions de haut niveau utilisant le Lidar : nettoyage des points et détection d'obsctacles
 */
public interface ILidarService {

    void refreshDetectedPoints();

    void refreshObstacles();

    void refreshObstacles(List<Line2D> lines);

    List<Point> getDetectedPointsMm();

    List<Shape> getCollisionsShape();

    boolean hasObstacle();

    void waitCleanup() throws InterruptedException;

    boolean mustCleanup();

}
