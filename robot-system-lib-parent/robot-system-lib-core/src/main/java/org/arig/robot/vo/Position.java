package org.arig.robot.vo;

import lombok.Data;

/**
 * The Class Position.
 * <p>
 * En vu de dessus :
 * <p>
 * y (2000) |
 * |
 * |
 * |
 * |
 * |---------------------------------- x (3000)
 * 0,0
 * <p>
 * angle = 0 dans le sens de X
 *
 * @author gdepuille
 */
@Data
public class Position {

    /**
     * The pt.
     */
    private Point pt;

    /**
     * The angle.
     */
    private double angle;

    /**
     * Instantiates a new robot position.
     */
    public Position() {
        pt = new Point();
        updatePosition(0, 0, 0);
    }

    /**
     * Update position.
     *
     * @param x     the x
     * @param y     the y
     * @param angle the angle
     */
    public void updatePosition(final double x, final double y, final double angle) {
        pt.setX(x);
        pt.setY(y);
        setAngle(angle);
    }

    /**
     * Adds the delta x.
     *
     * @param dX the d x
     */
    public void addDeltaX(final double dX) {
        pt.addDeltaX(dX);
    }

    /**
     * Adds the delta y.
     *
     * @param dY the d y
     */
    public void addDeltaY(final double dY) {
        pt.addDeltaY(dY);
    }
}
