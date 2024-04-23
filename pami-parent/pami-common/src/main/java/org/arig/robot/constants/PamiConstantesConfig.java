package org.arig.robot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

public interface PamiConstantesConfig {

    // Seuil de detection pour l'alimentation
    double seuilAlimentationServosVolts = 3;
    double seuilAlimentationMoteursVolts = 7;

    // Nb Thread Pool Scheduler
    int nbThreadScheduledExecutor = 20;

    // Nb Thread Pool Async
    int nbThreadAsyncExecutor = 10;

    // Configuration asservissement //
    double asservTimeMs = 20;
    double asservTimeS = PamiConstantesConfig.asservTimeMs / 1000;

    int i2cReadTimeMs = 20;
    double calageGlobalTimeMs = 100;
    double calageCourtTimeMs = 10;

    // -------------------------------- //
    // Configuration moteurs propulsion //
    // -------------------------------- //
    int numeroMoteurGauche = 2;
    int numeroMoteurDroit = 1;

    // ----------------------------- //
    // Configuration des convertions //
    // ----------------------------- //

    //double entraxe = 280;
    double entraxe = 279.4632765693719;

    // Diam <d_roue> mm => périmètre <xx> mm
    // <pulse_tour> p => <distance_tour> mm : <pulse_tour> / <distance_tour> = countPerMm
    double countPerMm = 1.0;

    // Entraxe 280mm => périmètre 879.64594300514200000 mm (1 roue)
    // 879.64594300514200000 mm => 180° : 879.64594300514200000 * 27.74036795337890000 / 180 =
    double countPerDeg = 1; // 10 tours

    double coefCodeurDroit = 1.0;
    double coefCodeurGauche = 1.0;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    double gainVitesseRampeDistance = 1.5;
    double gainVitesseRampeOrientation = gainVitesseRampeDistance * 2;
    double gainVitesseRampeDistanceSimulateur = 3;
    double gainVitesseRampeOrientationSimulateur = gainVitesseRampeDistanceSimulateur * 2;

    double rampAccDistance = 300.0; // en mm/s2
    double rampDecDistance = 300.0; // en mm/s2

    double rampAccOrientation = 300.0; // en mm/s2
    double rampDecOrientation = 300.0; // en mm/s2

    // -------------------------- //
    // Configuration des vitesses //
    // -------------------------- //

    long vitesseOrientationMax = 1000;
    long vitesseOrientationMin = 150;

    long vitesseMax = 1000;
    long vitesseMin = 100;

    // -------------- //
    // Parametres PID //
    // -------------- //
    double kcrDistance = 21.5;
    double tcrDistance = 0.04;
    double kpDistance = 70;
    double kiDistance = 5;
    double kdDistance = 300;
    double kpDistanceSimu = 12.9;
    double kiDistanceSimu = 0.0;
    double kdDistanceSimu = 0.008;

    double kcrOrientation = 10.0;
    double tcrOrientation = 0.05;
    double kpOrientation = 70;
    double kiOrientation = 5;
    double kdOrientation = 300;
    double kpOrientationSimu = 6.0;
    double kiOrientationSimu = 0.0;
    double kdOrientationSimu = 0.01;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    double arretDistanceMm = 1;
    double arretOrientDeg = 0.5;
    double approcheAvecFreinDistanceMm = 10;
    double approcheAvecFreinOrientationDeg = 5;
    double approcheSansFreinDistanceMm = 50;
    double approcheSansFreinOrientationDeg = 5;
    double startAngleDemiTourDeg = 75;
    double startAngleLimitVitesseDistance = 15;

    // TODO : A déterminer de manière empirique
    double seuilErreurOrientationDeg = arretOrientDeg;
    double seuilErreurDistanceMm = arretDistanceMm;

    double maxErrorSumDistance = 20000;
    double maxErrorSumOrientation = 40000;

    // -------------------------- //
    // Paramètre Physiques        //
    // -------------------------- //

    double dstCallage = 110.0; // dos du robot <=> milieu du robot

    // -------------------------- //
    // Paramètre Avoiding service //
    // -------------------------- //

    int avoidanceWaitTimeMs = 500;
    int avoidanceWaitTimeLongMs = 5000;
    int avoidancePathRefreshTimeMs = 2000;

    // ---------------------- //
    // Paramètre path finding //
    // ---------------------- //
    PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.ANYA16;
    int pathFindingAngle = 45;
    int pathFindingAngleSafe = 50;
    int pathFindingSeuilProximite = 440;
    int pathFindingSeuilProximiteSafe = 500;
    int pathFindingSeuilProximiteArig = 390;
    int pathFindingTailleObstacle = PamiConstantesConfig.pathFindingSeuilProximite * 2 + 50;
    int pathFindingTailleObstacleArig = PamiConstantesConfig.pathFindingSeuilProximiteArig * 2 + 50;

    int lidarClusterSizeMm = 50;
    int lidarOffsetPointMm = 30; // "recule" les points détectés pour prendre en compte qu'on ne detecte que les faces avant

    // ----------------- //
    // Paramètres métier //
    // ----------------- //

    int WAIT_LED = 200;
    int TIMEOUT_POMPE = 1000;
    int TIMEOUT_COLOR = 800;
}
