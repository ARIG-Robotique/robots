package org.arig.robot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

public interface OdinConstantesConfig {

    // Seuil de detection pour l'alimentation
    double seuilAlimentationServosVolts = 3;
    double seuilAlimentationMoteursVolts = 7;

    // Nb Thread Pool Scheduler
    int nbThreadScheduledExecutor = 20;

    // Nb Thread Pool Async
    int nbThreadAsyncExecutor = 10;

    // Configuration asservissement //
    double asservTimeMs = 20;
    double asservTimeS = OdinConstantesConfig.asservTimeMs / 1000;

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

    // Diam 47mm => périmètre 147.655 mm
    // 4096 p => 147.65485471872 mm : 4096 / 147.65485471872000000 = 27.74036795337890000
    //double countPerMm = 27.74036795337890000; // Théorique
    double countPerMm = 27.556656907330034; // Manuel 2700 -> mesure 2718 depuis le théorique

    // Entraxe 280mm => périmètre 879.64594300514200000 mm (1 roue)
    // 879.64594300514200000 mm => 180° : 879.64594300514200000 * 27.74036795337890000 / 180 =
    //double countPerDeg = 135.56501182033100000; // Théorique
    //double countPerDeg = 133.86663837508056; // 5 tours
    double countPerDeg = 133.86778649410599; // 10 tours

    double coefCodeurDroit = 1.0;
    double coefCodeurGauche = (0.9989161414636104 + 0.998509395055139) / 2;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    double gainVitesseRampeDistance = 1.5;
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
    double kcrDistance = 21.5;
    double tcrDistance = 0.04;
    double kpDistance = 30;
    double kiDistance = 0;
    double kdDistance = 50;
    double kpDistanceSimu = 12.9;
    double kiDistanceSimu = 0.0;
    double kdDistanceSimu = 0.008;

    double kcrOrientation = 10.0;
    double tcrOrientation = 0.05;
    double kpOrientation = 10;
    double kiOrientation = 0;
    double kdOrientation = 50;
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
    double seuilErreurOrientationDeg = 1;
    double seuilErreurDistanceMm = 1;

    // -------------------------- //
    // Paramètre Physiques        //
    // -------------------------- //

    double dstCallage = 110.0; // dos du robot <=> milieu du robot

    // -------------------------- //
    // Paramètre Avoiding service //
    // -------------------------- //

    int avoidanceWaitTimeMs = 500;
    int avoidancePathRefreshTimeMs = 2000;

    // ---------------------- //
    // Paramètre path finding //
    // ---------------------- //
    PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.ANYA16;
    int pathFindingAngle = 45;
    int pathFindingAngleSafe = 50;
    int pathFindingSeuilProximite = 480;
    int pathFindingSeuilProximiteSafe = 540;
    int pathFindingTailleObstacle = OdinConstantesConfig.pathFindingSeuilProximite * 2 + 50;

    int lidarClusterSizeMm = 50;
    int lidarOffsetPointMm = 30; // "recule" les points détectés pour prendre en compte qu'on ne detecte que les faces avant

    // ----------------- //
    // Paramètres métier //
    // ----------------- //

    int WAIT_LED = 200;
    int TIMEOUT_POMPE = 1000;
    int TIMEOUT_COLOR = 800;
}
