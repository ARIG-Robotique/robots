package org.arig.robot.system.bras;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.arig.robot.model.Point3D;

/**
 * Gestion des bras robotisé a n segment.
 * <p>
 * Par convention on adopte :
 * Axe des abscice : X
 * Axe des ordonnée : Z
 * <p>
 * Nous avons donc en coupe vertical le bras.
 * Le repere X,Y étant le repere du robot.
 *
 * @author gdepuille
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class AbstractBras implements IBrasManager {

    @Getter(AccessLevel.PROTECTED)
    private final Point3D P1;

    /**
     * Retourne l'angle entre a et b avec les longueurs des segments connu.
     *
     * @param a longueur du segment a
     * @param b longueur du segment b
     * @param c longueur du segment c
     *
     * @return Gamma en Radian
     */
    protected double alKashiAngleRad(double a, double b, double c) {
        return Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b));
    }

    protected double alKashiCote(double gamma, double a, double b) {
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) - (2 * a * b * Math.cos(gamma)));
    }
}
