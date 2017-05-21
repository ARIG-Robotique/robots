package org.arig.robot.system.pathfinding;

import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;

import java.awt.*;
import java.io.File;
import java.io.InputStream;

/**
 * @author gdepuille on 29/12/13.
 */
public interface IPathFinder {

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
     * Nombre de tuiles sur l'axe X
     *
     * @param nbTileX nombre de tuile sur l'axe X pour constuire le maillage
     */
    void setNbTileX(int nbTileX);

    /**
     * Nombre de tuiles sur l'axe Y
     *
     * @param nbTileY nombre de tuile sur l'axe Y pour constuire le maillage
     */
    void setNbTileY(int nbTileY);

    /**
     * Setter pour authorisé les déplacement en diagonale dans le graph
     *
     * @param allowDiagonal true pour autorisé les déplacement sur le maillage diagonal
     */
    void setAllowDiagonal(boolean allowDiagonal);

    /**
     * Initialisation du graph par une image en noir et blanc. Le noir représente les zones inaccessible.
     *
     * @param filePath Chemin de l'image source
     */
    void construitGraphDepuisImageNoirEtBlanc(String filePath);

    /**
     * Intialisation du graph par une image en noir et blanc. Le noir représente les zones inaccessible
     *
     * @param file Fichier image source
     */
    void construitGraphDepuisImageNoirEtBlanc(File file);

    /**
     * Intialisation du graph par une image en noir et blanc. Le noir représente les zones inaccessible
     *
     * @param is InputStream image source
     */
    void construitGraphDepuisImageNoirEtBlanc(InputStream is);

    /**
     * Ajout d'un obstacle sur la map.
     *
     * @param obstacles Une forme représentant les obstacles détecté.
     */
    void addObstacles(Shape... obstacles);

}
