package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gdepuille on 29/12/13.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {
    private double x;
    private double y;

    public void addDeltaX(final double dX) {
        x += dX;
    }
    public void addDeltaY(final double dY) {
        y += dY;
    }
}
