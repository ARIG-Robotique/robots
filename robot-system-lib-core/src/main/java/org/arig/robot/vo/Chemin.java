package org.arig.robot.vo;

import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Created by mythril on 29/12/13.
 */
public class Chemin {

    private Collection<Point> points;

    public Chemin() {
        points = new LinkedHashSet<>();
    }

    public void addPoint(final Point pt) {
        points.add(pt);
    }

    public Point getNextPoint() {
        if (!points.isEmpty()) {
            Iterator<Point> it = points.iterator();
            Point p = it.next();
            it.remove();
            return p;
        }

        // Fallback
        return null;
    }
}
