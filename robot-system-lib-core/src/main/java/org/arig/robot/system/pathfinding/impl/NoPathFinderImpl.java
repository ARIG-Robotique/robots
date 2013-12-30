package org.arig.robot.system.pathfinding.impl;

import org.arig.robot.system.pathfinding.AbstractPathFinder;
import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;

import java.io.File;

/**
 * /!\ Faux PathFinder
 * Cette implémentation ne calcul aucun chemin. La destination est tout de suite a atteindre.
 *
 * Created by mythril on 30/12/13.
 */
public class NoPathFinderImpl extends AbstractPathFinder<Integer> {

    @Override
    public Chemin findPath(Point from, Point to) {
        Chemin c = new Chemin();
        c.addPoint(to);

        return c;
    }

    @Override
    public void makeGraphFromBWImage(File file) {
        // NOP
    }
}
