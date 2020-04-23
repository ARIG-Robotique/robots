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

    // Temps écoulé pour la balise
    int baliseElapsedTimeMs = 50000;

    // Temps restant pour le déclenchement du pavillon
    int pavillonRemainingTimeMs = 5000;

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
    double rampDecDistance = 800.0; // en mm/s2

    double rampAccOrientation = 1000.0 * 2; // en mm/s2
    double rampDecOrientation = 1000.0 * 2; // en mm/s2

    // -------------------------- //
    // Configuration des vitesses //
    // -------------------------- //

    long vitesseOrientationUltraHaute = 1000 * 2;
    long vitesseOrientationSuperHaute = 800;
    long vitesseOrientationBasse = 300;
    long vitesseOrientationSuperBasse = 150;

    long vitesseUltraHaute = 1000;
    long vitesseSuperHaute = 800;
    long vitesseHaute = 600;
    long vitesseMoyenneHaute = 500;
    long vitesseMoyenneBasse = 400;
    long vitesseLente = 300;
    long vitesseSuperLente = 200;
    long vitesseUltraLente = 100;

    long vitessePath = vitesseSuperHaute;
    long vitesseOrientation = vitesseOrientationUltraHaute;

    // -------------- //
    // Parametres PID //
    // -------------- //

    double kcrDistance = 21.5;
    double tcrDistance = 0.04;
    double kpDistance = 12.9; // 12.90
    double kiDistance = 120 * asservTimeS; // 120
    double kdDistance = 0.002 / asservTimeS; // 0.002
    double kpDistanceSimu = kcrDistance * 0.6;
    double kiDistanceSimu = (tcrDistance * 0) * asservTimeS;
    double kdDistanceSimu = (tcrDistance / 5) / asservTimeS;

    double kcrOrientation = 10;
    double tcrOrientation = 0.05;
    double kpOrientation = kcrOrientation * 0.6;
    double kiOrientation = (tcrOrientation * 2) * asservTimeS;
    double kdOrientation = (tcrOrientation / 5) / asservTimeS;
    double kpOrientationSimu = kcrOrientation * 0.6;
    double kiOrientationSimu = (tcrOrientation * 0) * asservTimeS;
    double kdOrientationSimu = (tcrOrientation / 5) / asservTimeS;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    double arretDistanceMm = 1;
    double arretOrientDeg = 1;
    double approcheAvecFreinDistanceMm = 10;
    double approcheAvecFreinOrientationDeg = 5;
    double approcheSansFreinDistanceMm = 100;
    double approcheSansFreinOrientationDeg = 5;
    //double angleReculDeg = 0.45;
    double angleReculDeg = -1;

    // TODO : A déterminer de manière empirique
    double seuilErreurPidOrientation = 25000;
    double seuilErreurPidDistance = 25000;

    // -------------------------- //
    // Paramètre Physiques        //
    // -------------------------- //

    double dstCallageY = 151.0; // dos du robot <=> milieu du robot
    double dstDeposeArriereY = 223.5; // milieu de gobelets arrière, posés au sol <=> milieu du robot
    double dstDeposeAvantY = 125; // milieu des gobelets avant <=> milieu du robot
    double[] dstDeposeAvantX = new double[]{-130, -55, 55, 130};
    double dstBrasMancheAAirX = 210; // distance minimale pour pousser la manche à air

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
    PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.ANYA16;
    int pathFindingAngle = 45;
    int pathFindingSeuilProximite = 480;
    int pathFindingTailleObstacle = IConstantesNerellConfig.pathFindingSeuilProximite * 2 + 50;

    int lidarClusterSizeMm = 50;
    int lidarOffsetPointMm = 30; // "recule" les points détectés pour prendre en compte qu'on ne detecte que les faces avant

    // ----------------- //
    // Paramètres métier //
    // ----------------- //


}
