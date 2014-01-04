package org.arig.robot.system.bras;

import lombok.*;
import org.arig.robot.vo.Point3D;

/**
 * Gestion des bras robotisé a n segment.
 *
 * Par convention on adopte :
 *  Axe des abscice : X
 *  Axe des ordonnée : Z
 *
 *  Nous avons donc en coupe vertical le bras.
 *  Le repere X,Y étant le repere du robot.
 *
 * @author mythril
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class AbstractBras implements IBrasManager {

    @Getter(AccessLevel.PROTECTED)
    private final Point3D P1;

    /**
     * Retour l'angle entre a et b avec les longueurs des segment connu.
     *
     * @param a
     * @param b
     * @param c
     * @return Gamma en Radian
     */
    protected double alKashiAngleRad(double a, double b, double c) {
        return Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b));
    }

    protected double alKashiCote(double gamma, double a, double b) {
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) - (2 * a * b * Math.cos(gamma)));
    }
}
