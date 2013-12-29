package org.arig.robot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by mythril on 29/12/13.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {

    /** The x. */
    private double x;

    /** The y. */
    private double y;

    /**
     * Adds the delta x.
     *
     * @param dX
     *            the d x
     */
    public void addDeltaX(final double dX) {
        x += dX;
    }

    /**
     * Adds the delta y.
     *
     * @param dY
     *            the d y
     */
    public void addDeltaY(final double dY) {
        y += dY;
    }
}
