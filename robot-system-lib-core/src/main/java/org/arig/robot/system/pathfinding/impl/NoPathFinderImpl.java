package org.arig.robot.system.pathfinding.impl;

import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.system.pathfinding.AbstractPathFinder;
import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;

import java.io.File;
import java.util.LinkedList;

/**
 * /!\ Faux PathFinder
 * Cette implémentation ne calcul aucun chemin. La destination est tout de suite a atteindre.
 *
 * Created by mythril on 30/12/13.
 */
public class NoPathFinderImpl extends AbstractPathFinder {

    @Override
    public Chemin findPath(Point from, Point to) throws NoPathFoundException {
        return findPath(from, to, 0.0f);
    }

    @Override
    public Chemin findPath(Point from, Point to, float maxDistance) throws NoPathFoundException {
        Chemin c = new Chemin();
        c.addPoint(to);
        return c;
    }

    @Override
    public void construitGraphDepuisImageNoirEtBlanc(File file) {
        super.setMapSource(file);
    }
}
