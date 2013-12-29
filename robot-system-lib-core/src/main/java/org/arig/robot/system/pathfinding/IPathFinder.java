package org.arig.robot.system.pathfinding;

import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;

/**
 * Created by mythril on 29/12/13.
 */
public interface IPathFinder {

    /**
     * Méthode pour réaliser une detection de chemin a emprunter.
     *
     * @param from Point d'origine
     * @param to Point a atteindre
     * @return Le chemin a emprunter pour atteindre la cible en evitant les obtacles.
     */
    Chemin findPath(Point from, Point to);

    /**
     * Initialisation du graph par une image en noir et blanc. Le noir représente les zone inaccessible.
     *
     * @param imgData
     * @param tileX
     * @param tileY
     * @param allowDiagonal
     */
    void makeGraphFromBWImage(byte [] imgData, int tileX, int tileY, boolean allowDiagonal);
}
