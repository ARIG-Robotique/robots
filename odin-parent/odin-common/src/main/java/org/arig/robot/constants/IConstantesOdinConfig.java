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

    // Diam 47mm => périmètre 147.655 mm
    // 4096 p => 147.65485471872 mm : 4096 / 147.65485471872 = 27.740367953379
    //double countPerMm = 27.740367953379;
    //double countPerMm = 27.533863974;
    double countPerMm = 27.437735771;
    //double countPerMm = 27.4148709911908000;

    // Entraxe 250mm => périmètre 785.39816339745 mm (1 roue)
    // 785.39816339745 mm => 180° : 785.39816339745 * 27.740367953379 / 180 = 121.040189125
    //double countPerDeg = 121.04018912529;
    //double countPerDeg = 120.139145534;
    double countPerDeg = 119.719707124;
    //double countPerDeg = 119.6199407014400000;

    //double coefCodeurDroit = 1.0;
    //double coefCodeurGauche = 1.0;
    double coefCodeurDroit = 0.9979078108395320;
    double coefCodeurGauche = 1.0020921891604700;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    double gainVitesseRampeDistance = 1.5;
    double gainVitesseRampeOrientation = gainVitesseRampeDistance * 2;
    double gainVitesseRampeDistanceSimulateur = 1;
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
    long vitesseMin = 50;


    // -------------- //
    // Parametres PID //
    // -------------- //
    // TODO Configuration a faire
    double kcrDistance = 21.5;
    double tcrDistance = 0.04;
    double kpDistance = 30;   // 6.5;   // 12.90
    double kiDistance = 0; // 0.025; // 120
    double kdDistance = 50;   // 150;   // 0.002
    double kpDistanceSimu = 12.9;
    double kiDistanceSimu = 0.0;
    double kdDistanceSimu = 0.008;

    double kcrOrientation = 10.0;
    double tcrOrientation = 0.05;
    double kpOrientation = 10;    // 0.8;   // 5;
    double kiOrientation = 0; // 0.005; // 0.09;
    double kdOrientation = 50;   // 80;    // 50;
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

    double dstCallage = 91.0; // dos du robot <=> milieu du robot
    double[] dstDeposeX = new double[]{-38, 38};
    double dstDeposeY = 200; //TODO  milieu des gobelets avant <=> milieu du robot
    double dstBrasMancheAAirX = 210; // TODO distance minimale pour pousser la manche à air

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
    int pathFindingSeuilProximite = 440;
    int pathFindingSeuilProximiteSafe = 500;
    int pathFindingTailleObstacle = IConstantesOdinConfig.pathFindingSeuilProximite * 2 + 50;

    int lidarClusterSizeMm = 50;
    int lidarOffsetPointMm = 30; // "recule" les points détectés pour prendre en compte qu'on ne detecte que les faces avant

    // ----------------- //
    // Paramètres métier //
    // ----------------- //

    int WAIT_LED = 200;
    int WAIT_POMPES = 300;

    int TIME_BEFORE_READ_COLOR = 800;
}
