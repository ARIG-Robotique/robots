package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.math4.legacy.ml.clustering.Clusterable;

/**
 * @author gdepuille on 29/12/13.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point implements Clusterable {
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

    public Point offsetted(double dX, double dY) {
        return new Point(x + dX, y + dY);
    }

    public Point offsettedX(double dX) {
        return new Point(x + dX, y);
    }

    public Point offsettedY(double dY) {
        return new Point(x, y + dY);
    }

    public double distance(Point other) {
        double dX = other.getX() - x;
        double dY = other.getY() - y;
        return Math.sqrt(dX * dX + dY * dY);
    }

    public double angle(Point other) {
        double dX = x - other.getX();
        double dY = y - other.getY();
        return Math.toDegrees(Math.atan2(dY, dX));
    }

    @JsonIgnore
    @Override
    public double[] getPoint() {
        return new double[]{x, y};
    }
}
