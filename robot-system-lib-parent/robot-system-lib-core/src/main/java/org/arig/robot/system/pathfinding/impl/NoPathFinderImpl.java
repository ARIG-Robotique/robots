package org.arig.robot.system.pathfinding.impl;

import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.arig.robot.system.pathfinding.AbstractPathFinder;

import java.awt.*;
import java.io.InputStream;

/**
 * /!\ Faux PathFinder pour des test de mouvement lors de la phase de réglage
 * Cette implémentation ne calcul aucun chemin. La destination est tout de suite a atteindre.
 *
 * @author gdepuille on 30/12/13.
 */
public class NoPathFinderImpl extends AbstractPathFinder {

    @Override
    public void addObstacles(Shape... obstacles) {
    }

    @Override
    public Chemin findPath(Point from, Point to) throws NoPathFoundException {
        return findPath(from, to, 0.0f);
    }

    @Override
    public Chemin findPath(Point from, Point to, double maxDistance) throws NoPathFoundException {
        Chemin c = new Chemin();
        c.addPoint(to);
        return c;
    }

    @Override
    public void construitGraphDepuisImageNoirEtBlanc(final InputStream is) {
    }
}
