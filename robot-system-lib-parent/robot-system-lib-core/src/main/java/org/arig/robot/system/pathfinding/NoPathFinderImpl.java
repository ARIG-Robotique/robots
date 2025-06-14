package org.arig.robot.system.pathfinding;

import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;

import java.awt.*;
import java.io.InputStream;
import java.util.List;

/**
 * /!\ Faux PathFinder pour des test de mouvement lors de la phase de réglage
 * Cette implémentation ne calcul aucun chemin. La destination est tout de suite a atteindre.
 */
public class NoPathFinderImpl extends AbstractPathFinder {

  @Override
  public void setObstacles(final List<Shape> obstacles) {
  }

  @Override
  public Chemin findPath(Point from, Point to) throws NoPathFoundException {
    Chemin c = new Chemin();
    c.addPoint(to);
    return c;
  }

  @Override
  public boolean isBlocked(Point point) {
    return false;
  }

  @Override
  public Point getNearestPoint(Point from, Point to) {
    return to;
  }

  @Override
  public boolean isBlockedByObstacle(Point pointCm) {
    return false;
  }

  @Override
  public void construitGraphDepuisImageNoirEtBlanc(final InputStream is) {
  }
}
