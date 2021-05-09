package org.arig.robot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

public interface IConstantesOdinConfig {

    // Nb Thread Pool Scheduler
    int nbThreadScheduledExecutor = 20;

    // Nb Thread Pool Async
    int nbThreadAsyncExecutor = 10;

    // Configuration asservissement //
    double asservTimeMs = 20;
    double asservTimeS = IConstantesOdinConfig.asservTimeMs / 1000;

    int i2cReadTimeMs = 50;
    double calageTimeMs = 200;

    // -------------------------------- //
    // Configuration moteurs propulsion //
    // -------------------------------- //
    int numeroMoteurGauche = 2;
    int numeroMoteurDroit = 1;

    // ----------------------------- //
    // Configuration des convertions //
    // ----------------------------- //

    // TODO Configuration a faire
    // 40000 p => 1524.5 mm : 40000 / 1524.5 = 26,238110856
    double countPerMm = 26.238110856;

    // 51325 p => 360° : 51325 / 360 = 142,569444444
    double countPerDeg = 142.569444444;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    double gainVitesseRampeDistance = 3;
    double gainVitesseRampeOrientation = gainVitesseRampeDistance * 2;
    double gainVitesseRampeDistanceSimulateur = 3;
    double gainVitesseRampeOrientationSimulateur = gainVitesseRampeDistanceSimulateur * 2;

    double rampAccDistance = 1000.0; // en mm/s2
    double rampDecDistance = 1000.0; // en mm/s2

    double rampAccOrientation = 1000.0; // en mm/s2
    double rampDecOrientation = 1000.0; // en mm/s2

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
    // TODO Configuration a faire
    double kcrDistance = 21.5;
    double tcrDistance = 0.04;
    double kpDistance = 9;   // 6.5;   // 12.90
    double kiDistance = 0; // 0.025; // 120
    double kdDistance = 1;   // 150;   // 0.002
    double kpDistanceSimu = 12.9;
    double kiDistanceSimu = 0.0;
    double kdDistanceSimu = 0.008;

    double kcrOrientation = 10.0;
    double tcrOrientation = 0.05;
    double kpOrientation = 4;    // 0.8;   // 5;
    double kiOrientation = 0; // 0.005; // 0.09;
    double kdOrientation = 2;   // 80;    // 50;
    double kpOrientationSimu = 6.0;
    double kiOrientationSimu = 0.0;
    double kdOrientationSimu = 0.01;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    double arretDistanceMm = 1;
    double arretOrientDeg = 1;
    double approcheAvecFreinDistanceMm = 10;
    double approcheAvecFreinOrientationDeg = 5;
    double approcheSansFreinDistanceMm = 50;
    double approcheSansFreinOrientationDeg = 5;
    double startAngleDemiTourDeg = 75;
    double startAngleLimitVitesseDistance = 15;

    // TODO : A déterminer de manière empirique
    double seuilErreurOrientationDeg = 1;
    double seuilErreurDistanceMm = 1;

    // -------------------------- //
    // Paramètre Physiques        //
    // -------------------------- //

    double dstCallageY = 151.0; // dos du robot <=> milieu du robot
    double dstDeposeArriereY = 223.5; // milieu de gobelets arrière, posés au sol <=> milieu du robot
    double dstDeposeAvantY = 125; // milieu des gobelets avant <=> milieu du robot // FIXME nouvelle face avant
    double[] dstDeposeAvantX = new double[]{-130, -55, 55, 130};
    double dstBrasMancheAAirX = 210; // distance minimale pour pousser la manche à air

    // -------------------------- //
    // Paramètre Avoiding service //
    // -------------------------- //

    int avoidanceWaitTimeMs = 1000;
    int avoidancePathRefreshTimeMs = 2000;

    // ---------------------- //
    // Paramètre path finding //
    // ---------------------- //
    PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.ANYA16;
    int pathFindingAngle = 45;
    int pathFindingAngleSafe = 50;
    int pathFindingSeuilProximite = 480;
    int pathFindingSeuilProximiteSafe = 540;
    int pathFindingTailleObstacle = IConstantesOdinConfig.pathFindingSeuilProximite * 2 + 50;

    int lidarClusterSizeMm = 50;
    int lidarOffsetPointMm = 30; // "recule" les points détectés pour prendre en compte qu'on ne detecte que les faces avant

    // ----------------- //
    // Paramètres métier //
    // ----------------- //

    int WAIT_LED = 50;
    int WAIT_ASPIRATION = 300;
    int WAIT_EXPIRATION = 300;
}
