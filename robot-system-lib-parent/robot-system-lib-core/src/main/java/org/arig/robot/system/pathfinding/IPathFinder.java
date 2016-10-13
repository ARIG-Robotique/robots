package org.arig.robot.system.pathfinding;

import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;

import java.awt.*;
import java.io.File;

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
     * Méthode pour réaliser une detection de chemin a emprunter.
     *
     * @param from        Point d'origine
     * @param to          Point a atteindre
     * @param maxDistance Distance max pour trouver un noeud.
     *
     * @return Le chemin a emprunter pour atteindre la cible en evitant les obtacles.
     */
    Chemin findPath(Point from, Point to, double maxDistance) throws NoPathFoundException;

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
     * Ajout d'un obstacle sur la map.
     *
     * @param obstacles Les polygones représentant les obstacles détecté.
     */
    void addObstacles(Polygon... obstacles);

}
