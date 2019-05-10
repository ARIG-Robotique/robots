package org.arig.robot.utils;

import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Point;

public enum NerellUtils {
    ;

    /**
     * Retourne l'angle à utiliser dans {@link org.arig.robot.system.TrajectoryManager#alignFrontToAvecDecalage(double, double, double)}
     * pour qu'on point tombe dans une pince
     */
    public static double getAngleDecallagePince(Point from, Point to, ESide side) {
        double distance = from.distance(to);

        double angle = Math.asin(IConstantesNerellConfig.dstAtomeCentre / distance);

        return ESide.DROITE.equals(side) ? -angle : angle;
    }

    public static double getDistance(Point from, Point to) {
        return Math.sqrt(
                Math.pow(to.getX() - from.getX(), 2) +
                        Math.pow(to.getY() - from.getY(), 2)
        );
    }
}
