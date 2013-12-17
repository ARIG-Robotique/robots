package org.arig.robot.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import org.arig.robot.vo.enums.TypeConsigne;

/**
 * The Class RobotConsigne.
 * 
 * y (2000) |
 *          |
 *          |
 *          |
 *          |
 *          |---------------------------------- x (3000)
 *         0,0
 *
 * Theta = 0 dans le sens de X
 * 
 * @author mythril
 */
@Data
public class RobotConsigne {

    /** The x. */
    private double x;

    /** The y. */
    private double y;

    /** The angle. */
    private double angle;

    /** The consigne distance. */
    private long consigneDistance;

    /** The vitesse distance. */
    private long vitesseDistance;

    /** The consigne orientation. */
    private long consigneOrientation;

    /** The vitesse orientation. */
    private long vitesseOrientation;

    /** The cmd droit. */
    private int cmdDroit;

    /** The cmd gauche. */
    private int cmdGauche;

    /** The frein. */
    private boolean frein;

    /** The type. */
    @Getter(AccessLevel.NONE)
    private final List<TypeConsigne> types = new ArrayList<>();

    /**
     * Instantiates a new robot consigne.
     */
    public RobotConsigne() {
        x = y = angle = 0;
        consigneDistance = 0;
        consigneOrientation = 0;
        vitesseDistance = 100;
        vitesseOrientation = 100;
        cmdDroit = 0;
        cmdGauche = 0;
        frein = true;
        setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    /**
     * Sets the types.
     * 
     * @param values
     *            the new types
     */
    public void setTypes(final TypeConsigne... values) {
        types.clear();
        for (final TypeConsigne tc : values) {
            types.add(tc);
        }
    }

    /**
     * Checks if is type.
     * 
     * @param t
     *            the type
     * @return true, if is type
     */
    public boolean isType(final TypeConsigne t) {
        return types.contains(t);
    }

    /**
     * Checks if is all types.
     * 
     * @param types
     *            the types
     * @return true, if is all types
     */
    public boolean isAllTypes(final TypeConsigne... types) {
        boolean result = true;
        for (final TypeConsigne t : types) {
            result = result & isType(t);
        }

        return result;
    }
}