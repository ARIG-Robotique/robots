package org.arig.robot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

/**
 * @author gdepuille on 29/04/15.
 */
public interface IConstantesNerellConfig {

    // Nb Thread Pool Scheduler
    int nbThreadScheduledExecutor = 10;

    // Nb Thread Pool Async
    int nbThreadAsyncExecutor = 10;

    // Configuration asservissement //
    double asservTimeMs = 10;

    // Durée du match //
    int matchTimeMs = 89800;

    // Valeurs min / max en mm des axes du repères
    int minX = 170;
    int minY = 100;
    int maxX = 1180 - 100;
    int maxY = 1800 - 100;

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
    double rampAccDistance = 1000.0; // en mm/s2
    double rampDecDistance = 800.0; // en mm/s2

    double rampAccOrientation = 1000.0; // en mm/s2
    double rampDecOrientation = 1000.0; // en mm/s2

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

    double kpDistance = 0.5;
    double kiDistance = 0.0;
    double kdDistance = 0.05;

    double kpOrientation = 0.5;
    double kiOrientation = 0.0;
    double kdOrientation = 0.05;

    double kpMotDroit = 0.9;
    double kiMotDroit = 0.5;
    double kdMotDroit = 0.0009;

    double kpMotGauche = 0.9;
    double kiMotGauche = 0.5;
    double kdMotGauche = 0.0009;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    double arretDistanceMm = 1;
    double arretOrientDeg = 1;
    double approcheDistanceMm = 50;
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
    int invalidActionTimeSecond = 2;

    // ----------------- //
    // Paramètres métier //
    // ----------------- //
    int nbModuleMax = 4;

}
