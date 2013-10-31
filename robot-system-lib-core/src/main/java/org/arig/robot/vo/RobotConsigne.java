package org.arig.robot.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.arig.robot.vo.enums.TypeConsigne;

/**
 * The Class RobotConsigne.
 * 
 *  y (2000)
 *  |
 *  |
 *  |
 *  |
 *  |
 *  |---------------------------------- x (3000)
 * 0,0
 * Theta = 0 dans le sens de X
 * 
 * @author mythril
 */
public class RobotConsigne {

    /** Gets the position. */
    @Getter
    private final RobotPosition position;

    /** Gets the consigne polaire. */
    @Getter
    private final ConsignePolaire consignePolaire;

    /** The type. */
    private final List<TypeConsigne> types = new ArrayList<>();

    /**
     * Instantiates a new robot consigne.
     */
    public RobotConsigne() {
        position = new RobotPosition();
        consignePolaire = new ConsignePolaire();
        consignePolaire.setFrein(true);
        setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    /**
     * Sets the types.
     *
     * @param values the new types
     */
    public void setTypes(final TypeConsigne ... values) {
        types.clear();
        for (final TypeConsigne tc : values) {
            types.add(tc);
        }
    }

    /**
     * Checks if is type.
     *
     * @param t the type
     * @return true, if is type
     */
    public boolean isType(final TypeConsigne t) {
        return types.contains(t);
    }

    /**
     * Checks if is all types.
     *
     * @param types the types
     * @return true, if is all types
     */
    public boolean isAllTypes(final TypeConsigne ... types) {
        boolean result = true;
        for (final TypeConsigne t : types) {
            result = result & isType(t);
        }

        return result;
    }

    /**
     * Enable frein.
     */
    public void enableFrein() {
        consignePolaire.setFrein(true);
    }

    /**
     * Disable frein.
     */
    public void disableFrein() {
        consignePolaire.setFrein(false);
    }

    /**
     * Gets the frein.
     *
     * @return the frein
     */
    public boolean getFrein() {
        return consignePolaire.isFrein();
    }
}