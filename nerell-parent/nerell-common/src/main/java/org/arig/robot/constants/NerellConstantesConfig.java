package org.arig.robot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

public interface NerellConstantesConfig {

    // Seuil de detection pour l'alimentation
    double seuilAlimentationServosVolts = 3;
    double seuilAlimentationMoteursVolts = 7;

    // Nb Thread Pool Scheduler
    int nbThreadScheduledExecutor = 20;

    // Nb Thread Pool Async
    int nbThreadAsyncExecutor = 10;

    // Configuration asservissement //
    double asservTimeMs = 20;
    double asservTimeS = NerellConstantesConfig.asservTimeMs / 1000;

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

    //double entraxe = 280; // Mesuré
    //double entraxe = 279.8739932288727; // 5 tours (1: 280.3958334999998, 2: 279.8739932288727)
    //double entraxe = 279.7487166907122; // 50 tours (1: 280.1491881867973, 2: 279.7487166907122)

    // 29/04/2022
    //double entraxe = ; // 1 tour (1: 280.11787990374694, 2: 279.8743841087656, M: 279.9961320062563)
    //double entraxe = ; // 3 tours (1: 280.1538206903572, 2: 279.90520802694107, M: 280.0295143586491)
    //double entraxe = ; // 5 tours (1: 280.1739572404326, 2: 279.9955970798136, M: 280.0847771601231)
    //double entraxe = ; // 10 tours (1: 280.1866799431399, 2: 279.93264432000274, M: 280.0596621315713)
    //double entraxe = ; // 20 tours (1: 280.16442357229266, 2: 279.9582320272419, M: 280.06132779976724)
    //double entraxe = ; // 30 tours (1: 280.1720458960358, 2: 279.9663528029184, M: 280.0691993494771)
    //double entraxe = ; // 50 tours (1: 280.1867535816509, 2: 279.9567559204513, M: 280.0717547510511)
    double entraxe = 279.941310612305; // Moyenne des 2


    // Diam 47mm => périmètre 147.655 mm
    // 4096 p => 147.65485471872 mm : 4096 / 147.65485471872000000 = 27.74036795337890000
    //double countPerMm = 27.74036795337890000; // Théorique
    //double countPerMm = 27.536394659604056; // Manuel 2700 -> mesure 2720 depuis le théorique
    //double countPerMm = 27.387010963439838; // Manuel 2750 -> mesure 2765 depuis le manuel 2720
    double countPerMm = 27.37705567046876; // Manuel 2750 -> mesure 2751 depuis le manuel 2765

    // Entraxe 280mm => périmètre 879.64594300514200000 mm (1 roue)
    // 879.64594300514200000 mm => 180° : 879.64594300514200000 * 27.74036795337890000 / 180 =
    //double countPerDeg = 135.56501182033100000; // Théorique
    //double countPerDeg = 133.54528278505845; // 5 tours
    //double countPerDeg = 133.54984849251548; // 10 tours
    //double countPerDeg = 133.60106536759326; // 50 tours
    //double countPerDeg = 133.75398669244305; // 50 tours

    //double coefCodeurDroit = (0.9914760162438181 + 0.9901903052878885 + 0.9901004087212356) / 3;
    double coefCodeurDroit = 0.9928107224103835; //0.9920508329274527; //0.990588910084314;
    double coefCodeurGauche = 1;

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
    double kiDistance = 0.005;
    double kdDistance = 50;
    double kpDistanceSimu = 12.9;
    double kiDistanceSimu = 0.0;
    double kdDistanceSimu = 0.008;

    double kcrOrientation = 10.0;
    double tcrOrientation = 0.05;
    double kpOrientation = 30;
    double kiOrientation = 0.005;
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
    int avoidancePathRefreshTimeMs = 2000;

    // ---------------------- //
    // Paramètre path finding //
    // ---------------------- //
    PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.ANYA16;
    int pathFindingAngle = 45;
    int pathFindingAngleSafe = 50;
    int pathFindingSeuilProximite = 440;
    int pathFindingSeuilProximiteSafe = 500;
    int pathFindingSeuilProximiteArig = 400;
    int pathFindingTailleObstacle = NerellConstantesConfig.pathFindingSeuilProximite * 2 + 50;
    int pathFindingTailleObstacleArig = NerellConstantesConfig.pathFindingSeuilProximiteArig * 2 + 40;

    int lidarClusterSizeMm = 50;
    int lidarOffsetPointMm = 30; // "recule" les points détectés pour prendre en compte qu'on ne detecte que les faces avant

    // ----------------- //
    // Paramètres métier //
    // ----------------- //

    int WAIT_LED = 200;
    int TIMEOUT_POMPE = 1000;
    int TIMEOUT_COLOR = 800;
}
