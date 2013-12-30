package org.arig.robot.system.pathfinding;

import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;

import java.io.File;

/**
 * Created by mythril on 29/12/13.
 */
public interface IPathFinder<A> {

    /**
     * Méthode pour réaliser une detection de chemin a emprunter.
     *
     * @param from Point d'origine
     * @param to Point a atteindre
     * @return Le chemin a emprunter pour atteindre la cible en evitant les obtacles.
     */
    Chemin findPath(Point from, Point to);

    /**
     * Définition de l'algorithme
     *
     * @param algorithm
     */
    void setAlgorithm(A algorithm);

    /**
     * Nombre de tuiles sur l'axe X
     *
     * @param nbTileX
     */
    void setNbTileX(int nbTileX);

    /**
     * Nombre de tuiles sur l'axe Y
     *
     * @param nbTileY
     */
    void setNbTileY(int nbTileY);

    /**
     * Setter pour authorisé les déplacement en diagonale dans le graph
     *
     * @param allowDiagonal
     */
    void setAllowDiagonal(boolean allowDiagonal);

    /**
     * Initialisation du graph par une image en noir et blanc. Le noir représente les zones inaccessible.
     *
     * @param filePath
     */
    void makeGraphFromBWImage(String filePath);

    /**
     * Intialisation du graph par une image en noir et blanc. Le noir représente les zones inaccessible
     *
     * @param file
     */
    void makeGraphFromBWImage(File file);

}
