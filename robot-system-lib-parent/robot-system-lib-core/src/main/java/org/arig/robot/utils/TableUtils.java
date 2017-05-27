package org.arig.robot.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.Rectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
    private List<Rectangle> deadZones = new ArrayList<>();

    public TableUtils(int minXmm, int maxXmm, int minYmm, int maxYmm) {
        this.minXmm = minXmm;
        this.maxXmm = maxXmm;
        this.minYmm = minYmm;
        this.maxYmm = maxYmm;
    }

    public void clearDeadZones() {
        deadZones.clear();
    }

    public void addDeadZone(Rectangle r) {
        if (r != null) {
            deadZones.add(r);
        }
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

        boolean inDeadZones = false;
        if (CollectionUtils.isNotEmpty(deadZones)) {
            inDeadZones = deadZones.parallelStream().anyMatch(
                r -> pt.getX() >= r.getX() && pt.getX() <= r.getX() + r.getW()
                    && pt.getY() >= r.getY() && pt.getY() <= r.getY() + r.getH()
            );
        }

        return inTable && !inDeadZones;
    }

    /**
     * Définition d'un point (dans le repère table) en fonction d'une distance
     * et d'un angle par rapport à la position du robot
     *
     * @param distanceMm Distance par rapport au robot.
     * @param offsetAngle Valeur en degré par rapport au robot.
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
}
