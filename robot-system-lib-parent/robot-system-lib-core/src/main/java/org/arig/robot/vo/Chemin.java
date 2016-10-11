package org.arig.robot.vo;

import lombok.Data;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Définition par une liste de points du chemin a parcourir pour aller d'un point A au point B.
 *
 * Created by mythril on 29/12/13.
 */
@Data
public class Chemin {

    private final Collection<Point> points = new LinkedHashSet<>();

    public void addPoint(final Point pt) {
        points.add(pt);
    }

    public int nbPoints() {
        return points.size();
    }

    public boolean hasNext() {
        return !points.isEmpty();
    }

    public Point next() {
        Iterator<Point> it = points.iterator();
        if (it.hasNext()) {
            Point p = it.next();
            it.remove();
            return p;
        }

        // Fallback
        return null;
    }
}
