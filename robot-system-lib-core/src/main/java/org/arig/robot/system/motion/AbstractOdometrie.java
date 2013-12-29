package org.arig.robot.system.motion;

import lombok.AccessLevel;
import lombok.Getter;
import org.arig.robot.vo.Position;
import org.arig.robot.vo.enums.TypeOdometrie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class AbstractOdometrie.
 * 
 * @author mythril
 */
public abstract class AbstractOdometrie implements IOdometrie {

    /** The position. */
    @Autowired
    @Qualifier("currentPosition")
    @Getter(AccessLevel.PROTECTED)
    private Position position;

    /** The type. */
    @Getter
    private final TypeOdometrie type;

    /**
     * Instantiates a new odometrie.
     * 
     * @param type
     *            the type
     */
    protected AbstractOdometrie(final TypeOdometrie type) {
        this.type = type;
        initOdometrie(0, 0, 0);
    }

    /**
     * Inits the odometrie.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param angle
     *            the angle
     */
    @Override
    public void initOdometrie(final double x, final double y, final int angle) {
        position.updatePosition(x, y, angle);
    }

    /**
     * Calcul de la position en fonction de la valeurs des codeurs.
     * 
     * /!\ Cette méthode doit être appelé après la lecture des valeurs codeurs toutes les x ms.
     */
    @Override
    public void calculPosition() {
        process();

        // TODO : Loggeur CSV
        /*
         * #ifdef DEBUG_MODE Serial.print(";");Serial.print(Conv.pulseToMm(position.getX()));
         * Serial.print(";");Serial.print(Conv.pulseToMm(position.getY())); Serial.print(";");Serial.print((double)
         * Conv.pulseToDeg(position.getAngle())); #endif
         */
    }

    /**
     * Process.
     */
    protected abstract void process();
}
