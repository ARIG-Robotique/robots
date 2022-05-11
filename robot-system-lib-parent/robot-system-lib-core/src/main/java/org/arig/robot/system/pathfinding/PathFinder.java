package org.arig.robot.system.pathfinding;

import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

public interface PathFinder {

    /**
     * Méthode pour réaliser une detection de chemin a emprunter.
     *
     * @param from Point d'origine
     * @param to   Point a atteindre
     *
     * @return Le chemin a emprunter pour atteindre la cible en evitant les obtacles.
     */
    Chemin findPath(Point from, Point to) throws NoPathFoundException;

    /**
     * Intialisation du graph par une image en noir et blanc. Le noir représente les zones inaccessible
     *
     * @param is InputStream image source
     */
    void construitGraphDepuisImageNoirEtBlanc(InputStream is);

    boolean isBlocked(Point pointCm);

    boolean isBlockedByObstacle(Point pointCm);

    boolean isBordureTable(Point pointCm);

    /**
     * Ajout d'un obstacle sur la map.
     *
     * @param obstacles Une forme représentant les obstacles détecté.
     */
    void setObstacles(List<Shape> obstacles);

    BufferedImage getWorkImage();

}
