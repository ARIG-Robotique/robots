package org.arig.robot.model;

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
 * angle = 0 dans le sens de X dans le sens trigo
 *
 * @author gdepuille
 */
@Data
public class Position {

    private Point pt;
    private double angle;

    public Position() {
        pt = new Point();
        updatePosition(0, 0, 0);
    }

    public void updatePosition(final double x, final double y, final double angle) {
        pt.setX(x);
        pt.setY(y);
        setAngle(angle);
    }

    public void addDeltaX(final double dX) {
        pt.addDeltaX(dX);
    }
    public void addDeltaY(final double dY) {
        pt.addDeltaY(dY);
    }
}
