package org.arig.robot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

/**
 * @author gdepuille on 29/04/15.
 */
public interface IConstantesNerellConfig {

    // Nb Thread Pool Scheduler
    int nbThreadScheduledExecutor = 10;

    // Nb Thread Pool Async
    int nbThreadAsyncExecutor = 5;

    // Configuration asservissement //
    double asservTimeMs = 10;

    // Durée du match //
    int matchTimeMs = 89500;

    // Valeurs min / max en mm des axes du repères
    int minX = 170;
    int minY = 100;
    int maxX = 2000 - 100;
    int maxY = 3000 - 100;
    //int maxY = 1500;

    int minXEscalier = 0;
    int maxXEscalier = 450;
    int minYEscalier = 950;
    int maxYEscalier = 2050;

    // -------------------------------- //
    // Configuration moteurs propulsion //
    // -------------------------------- //
    int numeroMoteurGauche = 2;
    int numeroMoteurDroit = 1;

    // ----------------------------- //
    // Configuration des convertions //
    // ----------------------------- //

    // 40000 p => 1524.5 mm : 40000 / 1524.5 = 26,238110856
    double countPerMm = 26.238110856;

    // 51325 p => 360° : 51325 / 360 = 142,569444444
    double countPerDeg = 142.569444444;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    double rampAccDistance = 500.0; // en mm/s2
    double rampDecDistance = 500.0; // en mm/s2

    double rampAccOrientation = 800.0; // en mm/s2
    double rampDecOrientation = 800.0; // en mm/s2

    // -------------------------- //
    // Configuration des vitesses //
    // -------------------------- //

    long vitesseOrientation = 800;

    long vitesseSuperHaute = 750;
    long vitesseHaute = 600;
    long vitesseMoyenneHaute = 500;
    long vitesseMoyenneBasse = 400;
    long vitesseLente = 300;
    long vitesseSuperLente = 200;

    long vitessePath = vitesseSuperHaute;
    long vitesseMouvement = vitesseLente;

    // -------------- //
    // Parametres PID //
    // -------------- //

    //    double kpDistance = 0.75;
//    double kiDistance = 18.75;
//    double kdDistance = 0.0;
    double kpDistance = 1.5;
    double kiDistance = 4.0;
    double kdDistance = 0.00050;

    double kpOrientation = 0.5;
    double kiOrientation = 3.5;
    double kdOrientation = 0.0050;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    double distanceMiniEntrePointMm = 400;
    double distanceChangementVitesse = 700;
    double arretDistanceMm = 1;
    double arretOrientDeg = 1;
    double approcheDistanceMm = 10;
    double approcheOrientationDeg = 5;
    double angleReculDeg = 0.45;

    // -------------------------- //
    // Paramètre Avoiding service //
    // -------------------------- //
    enum AvoidingSelection {
        BASIC, FULL
    }

    // ---------------------- //
    // Paramètre path finding //
    // ---------------------- //
    PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.A_STAR_MANHATTAN;
    int invalidActionTimeSecond = 3;

    // ----------------- //
    // Paramètres métier //
    // ----------------- //
    int nbPiedMax = 4;

}
