package org.arig.eurobot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

/**
 * Created by gdepuille on 23/12/14.
 */
public interface IConstantesRobot {

    // Configuration asservissement //
    static final double asservTimeMs = 10;

    // Durée du match //
    static final int matchTimeMs = 89500;

    // Valeurs min / max en mm des axes du repères
    static final int minX = 170;
    static final int minY = 100;
    static final int maxX = 2000 - 100;
    static final int maxY = 3000 - 100;
    //static final int maxY = 1500;

    static final int minXEscalier = 0;
    static final int maxXEscalier = 450;
    static final int minYEscalier = 950;
    static final int maxYEscalier = 2050;

    // ----------------------------- //
    // Configuration des convertions //
    // ----------------------------- //

    // 40000 p => 1524.5 mm : 40000 / 1524.5 = 26,238110856
    static final double countPerMm = 26.238110856;

    // 51325 p => 360° : 51325 / 360 = 142,569444444
    static final double countPerDeg = 142.569444444;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    static final double rampAccDistance = 500.0; // en mm/s2
    static final double rampDecDistance = 500.0; // en mm/s2

    static final double rampAccOrientation = 800.0; // en mm/s2
    static final double rampDecOrientation = 800.0; // en mm/s2

    // -------------------------- //
    // Configuration des vitesses //
    // -------------------------- //

    static final long vitesseOrientation = 800;

    static final long vitesseHaute = 600;
    static final long vitesseMoyenne = 400;
    static final long vitesseLente = 200;
    static final long vitesseSuperLente = 100;

    static final long vitessePath = vitesseMoyenne;
    static final long vitesseMouvement = vitesseLente;

    // -------------- //
    // Parametres PID //
    // -------------- //

//    static final double kpDistance = 0.75;
//    static final double kiDistance = 18.75;
//    static final double kdDistance = 0.0;
    static final double kpDistance = 0.65;
    static final double kiDistance = 4.0;
    static final double kdDistance = 0.00050;

    static final double kpOrientation = 0.15;
    static final double kiOrientation = 3.5;
    static final double kdOrientation = 0.0050;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    static final double distanceMiniEntrePointMm = 400;
    static final double distanceChangementVitesse = 700;
    static final double arretDistanceMm = 1;
    static final double arretOrientDeg = 1;
    static final double approcheDistanceMm = 10;
    static final double approcheOrientationDeg = 5;
    static final double angleReculDeg = 0.45;

    // -------------------------- //
    // Paramètre Avoiding service //
    // -------------------------- //
    enum AvoidingSelection {
        BASIC, FULL
    }

    // ---------------------- //
    // Paramètre path finding //
    // ---------------------- //
    static final PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.A_STAR_MANHATTAN;

    // ----------------- //
    // Paramètres métier //
    // ----------------- //
    static final int nbPiedMax = 4;
}
