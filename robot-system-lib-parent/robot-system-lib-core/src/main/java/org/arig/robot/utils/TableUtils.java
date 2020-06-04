package org.arig.robot.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gdepuille on 14/05/17.
 */
public class TableUtils {

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    private final int minXmm, maxXmm, minYmm, maxYmm;
    private List<Rectangle.Double> persistentDeadZones = new ArrayList<>();
    private List<Rectangle.Double> dynamicDeadZones = new ArrayList<>();

    public TableUtils(int minXmm, int maxXmm, int minYmm, int maxYmm) {
        this.minXmm = minXmm;
        this.maxXmm = maxXmm;
        this.minYmm = minYmm;
        this.maxYmm = maxYmm;
    }

    public void clearDynamicDeadZones() {
        dynamicDeadZones.clear();
    }

    public void addPersistentDeadZone(Rectangle.Double r) {
        if (r != null) {
            persistentDeadZones.add(r);
        }
    }

    public void addDynamicDeadZone(Rectangle.Double r) {
        if (r != null) {
            dynamicDeadZones.add(r);
        }
    }

    public double distance(Point dest) {
        Point pos = new Point(position.getPt());
        pos.setX(conv.pulseToMm(pos.getX()));
        pos.setY(conv.pulseToMm(pos.getY()));
        return dest.distance(pos);
    }

    public int alterOrder(Point dest) {
        return (int) -Math.ceil(distance(dest) / 100);
    }

    /**
     * Controle que les coordonnées du point sont sur la table.
     *
     * @param pt Point avec des coordonnées en mm
     * @return true si le point est sur la table
     */
    public boolean isInTable(Point pt) {
        boolean inTable = pt.getX() > minXmm && pt.getX() < maxXmm
                && pt.getY() > minYmm && pt.getY() < maxYmm;

        boolean inPersistantDeadZones = false;
        if (CollectionUtils.isNotEmpty(persistentDeadZones)) {
            inPersistantDeadZones = persistentDeadZones.parallelStream().anyMatch(
                    r -> r.contains(new Point2D.Double(pt.getX(), pt.getY()))
            );
        }
        boolean inDynamicDeadZones = false;
        if (CollectionUtils.isNotEmpty(dynamicDeadZones)) {
            inDynamicDeadZones = dynamicDeadZones.parallelStream().anyMatch(
                    r -> r.contains(new Point2D.Double(pt.getX(), pt.getY()))
            );
        }

        return inTable && !inPersistantDeadZones && !inDynamicDeadZones;
    }

    public Polygon createPolygonObstacle(Point pt, double tailleObstacle) {
        int r1 = (int) (Math.cos(Math.toRadians(22.5)) * tailleObstacle / 2 / 10);
        int r2 = (int) (Math.sin(Math.toRadians(22.5)) * tailleObstacle / 2 / 10);

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

        return obstacle;
    }

    public Rectangle createRectangleObstacle(Point pt, double tailleObstacle) {
        return new Rectangle(
            (int) Math.round(pt.getX() / 10. - tailleObstacle / 10. / 2.),
            (int) Math.round(pt.getY() / 10. - tailleObstacle / 10. / 2.),
            (int) Math.round(tailleObstacle / 10.),
            (int) Math.round(tailleObstacle / 10.)
        );
    }

    /**
     * Définition d'un point (dans le repère table) en fonction d'une distance
     * et d'un angle par rapport à la position du robot
     *
     * @param distanceMm     Distance par rapport au robot.
     * @param offsetAngle    Valeur en degré par rapport au robot.
     * @param offsetXRobotMm Valeur en mm de décalage du capteur par rapport au robot sur l'axe X
     * @param offsetYRobotMm Valeur en mm de décalage du capteur par rapport au robot sur l'axe Y
     * @return Le point si présent sur la table, null sinon
     */
    public Point getPointFromAngle(double distanceMm, double offsetAngle, double offsetXRobotMm, double offsetYRobotMm) {
        // 1. Récupération du point dans le repère cartésien de la table
        double theta = conv.pulseToRad(position.getAngle()) + Math.toRadians(offsetAngle); // On calcul la position sur l'angle du repère pour n'avoir que la translation a faire
        Point ptObstacle = new Point();
        ptObstacle.setX(distanceMm * Math.cos(theta));
        ptObstacle.setY(distanceMm * Math.sin(theta));

        // 2. Translation du point de la position du robot
        ptObstacle.addDeltaX(conv.pulseToMm(position.getPt().getX()) + offsetXRobotMm);
        ptObstacle.addDeltaY(conv.pulseToMm(position.getPt().getY()) + offsetYRobotMm);

        return ptObstacle;
    }

    public Point getPointFromAngle(double distanceMm, double offsetAngle) {
        return getPointFromAngle(distanceMm, offsetAngle, 0, 0);
    }

    /**
     * Eloigne un point dans la direction du robot
     */
    public Point eloigner(Point point, double offset) {
        double currentX = conv.pulseToMm(position.getPt().getX());
        double currentY = conv.pulseToMm(position.getPt().getY());

        double dY = point.getY() - currentY;
        double dX = point.getX() - currentX;
        double angle = Math.atan2(dY, dX);
        double dist = Math.sqrt(dX * dX + dY * dY);

        Point ptObstacle = new Point();
        ptObstacle.setX((dist + offset) * Math.cos(angle));
        ptObstacle.setY((dist + offset) * Math.sin(angle));
        ptObstacle.addDeltaX(currentX);
        ptObstacle.addDeltaY(currentY);
        return ptObstacle;
    }
}
