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

    public Point(final Point point) {
        this(point.x, point.y);
    }

    public void addDeltaX(final double dX) {
        x += dX;
    }

    public void addDeltaY(final double dY) {
        y += dY;
    }

    public Point multiplied(double f) {
        return new Point(x * f, y * f);
    }

    public Point offsettedX(double dX) {
        return new Point(x + dX, y);
    }

    public Point offsettedY(double dY) {
        return new Point(x, y + dY);
    }
}
