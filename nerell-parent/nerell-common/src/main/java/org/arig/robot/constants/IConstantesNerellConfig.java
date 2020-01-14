package org.arig.robot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

public interface IConstantesNerellConfig {

    // Nb Thread Pool Scheduler
    int nbThreadScheduledExecutor = 20;

    // Nb Thread Pool Async
    int nbThreadAsyncExecutor = 10;

    // Configuration asservissement //
    double asservTimeMs = 20;
    double asservTimeS = IConstantesNerellConfig.asservTimeMs / 1000;

    double i2cReadTimeMs = 50;
    double calageTimeMs = 200;

    // Durée du match //
    int matchTimeMs = 99950;

    // Valeurs min / max en mm des axes du repères
    int minX = 50;
    int minY = 50;
    int maxX = 3000 - 50;
    int maxY = 2000 - 50;

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
    double rampAccDistance = 800.0; // en mm/s2
    double rampDecDistance = 500.0; // en mm/s2

    double rampAccOrientation = 800.0; // en mm/s2
    double rampDecOrientation = 500.0; // en mm/s2

    // -------------------------- //
    // Configuration des vitesses //
    // -------------------------- //

    long vitesseOrientationUltraHaute = 1000;
    long vitesseOrientationSuperHaute = 800;
    long vitesseOrientationBasse = 300;
    long vitesseOrientationSuperBasse = 150;

    long vitesseSuperHaute = 700;
    long vitesseHaute = 600;
    long vitesseMoyenneHaute = 500;
    long vitesseMoyenneBasse = 400;
    long vitesseLente = 300;
    long vitesseSuperLente = 200;
    long vitesseUltraLente = 100;

    long vitessePath = vitesseSuperHaute;
    long vitesseMouvement = vitesseSuperLente;
    long vitesseOrientation = vitesseOrientationUltraHaute;

    // -------------- //
    // Parametres PID //
    // -------------- //

    double kpDistance = 8;
    double kiDistance = 170 * asservTimeS;
    double kdDistance = 0.08 / asservTimeS;

    double kpOrientation = 2;
    double kiOrientation = 150 * asservTimeS;
    double kdOrientation = 0.1 / asservTimeS;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    double arretDistanceMm = 1;
    double arretOrientDeg = 1;
    double approcheDistanceMm = 100;
    double approcheOrientationDeg = 5;
    //double angleReculDeg = 0.45;
    double angleReculDeg = -1;

    double dstArriere = 162.5; // distance du dos du robot au milieu des roues

    double seuilErreurPidOrientation = 5000;
    double seuilErreurPidDistance = 5000;

    // -------------------------- //
    // Paramètre Avoiding service //
    // -------------------------- //
    enum AvoidingSelection {
        BASIC, FULL, NOT_BASIC, SEMI_FULL
    }
    int avoidanceWaitTimeMs = 1000;
    int avoidancePathRefreshTimeMs = 2000;

    // ---------------------- //
    // Paramètre path finding //
    // ---------------------- //
    PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.LAZY_THETA_STAR;
    int pathFindingAngle = 45;
    int pathFindingSeuilProximite = 480;
    int pathFindingTailleObstacle = IConstantesNerellConfig.pathFindingSeuilProximite * 2 + 50;

    int lidarClusterSizeMm = 50;
    int lidarOffsetPointMm = 30; // "recule" les points détectés pour prendre en compte qu'on ne detecte que les faces avant

    // --------------------- //
    // Paramètre pneumatique //
    // --------------------- //
    int tempsActivationElectrovanne = 200;

    // ----------------- //
    // Paramètres métier //
    // ----------------- //


}
