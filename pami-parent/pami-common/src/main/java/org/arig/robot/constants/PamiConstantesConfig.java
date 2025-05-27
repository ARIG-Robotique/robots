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
  //int numeroMoteurGauche = 2;
  //int numeroMoteurDroit = 1;

  // ----------------------------- //
  // Configuration des convertions //
  // ----------------------------- //

  double entraxeTriangle = 110.0094727205884;
  double entraxeCarre = 104.72747511715694;
  double entraxeRond = 105.33522951647504;
  double entraxeStar = 105.52939546733161;

  // Diam <d_roue> mm => périmètre <xx> mm
  // <pulse_tour> p => <distance_tour> mm : <pulse_tour> / <distance_tour> = countPerMm
  // 0 : 3.7901084122349
  // 1 : 3.542157394612056
  // 2 : 3.5433385074478716
  // 3 : 3.5421577881851434
  double countPerMm = (3.542157394612056 + 3.5433385074478716 + 3.5421577881851434) / 3;

  // Entraxe <entraxe>mm => périmètre <entraxe> * PI mm (1 roue)
  // <perimetre_roue> mm => <countPerMM> * <perimetre> / 180 = countPerDeg
  double countPerDeg = 9.09560723514212; // X4

  double coefCodeurDroitTriangle = 1.0;
  double coefCodeurGaucheTriangle = (0.9995061300101105 +  0.9988893648753708) / 2;
  double coefCodeurDroitCarre = 1.0;
  double coefCodeurGaucheCarre = 0.9907354676640796;
  double coefCodeurDroitRond = 1.0;
  double coefCodeurGaucheRond = 0.9952581446633484;
  double coefCodeurDroitStar = 0.9929760887532072;
  double coefCodeurGaucheStar = 1.0;

  // ------------------------ //
  // Configuration des rampes //
  // ------------------------ //
  double gainVitesseRampeDistance = 1.5;
  double gainVitesseRampeOrientation = gainVitesseRampeDistance * 2;
  double gainVitesseRampeDistanceSimulateur = 3;
  double gainVitesseRampeOrientationSimulateur = gainVitesseRampeDistanceSimulateur * 2;

  double rampAccDistance = 500.0; // en mm/s2
  double rampDecDistance = 300.0; // en mm/s2

  double rampAccOrientation = 500.0; // en mm/s2
  double rampDecOrientation = 300.0; // en mm/s2

  // -------------------------- //
  // Configuration des vitesses //
  // -------------------------- //

  long vitesseOrientationMax = 800;
  long vitesseOrientationMin = 300;

  long vitesseMax = 800;
  long vitesseMin = 300;

  // -------------- //
  // Parametres PID //
  // -------------- //
  double kpDistance = 90;
  double kiDistance = 3.0;
  double kdDistance = 125;
  double kpDistanceSimu = 12.9;
  double kiDistanceSimu = 0.1;
  double kdDistanceSimu = 0.008;

  double kpOrientation = 90;
  double kiOrientation = 2.0;
  double kdOrientation = 125;
  double kpOrientationSimu = 6.0;
  double kiOrientationSimu = 0.1;
  double kdOrientationSimu = 0.01;

  // --------------------------- //
  // Paramètre mouvement manager //
  // --------------------------- //
  double arretDistanceMm = 5;
  double arretOrientDeg = 3;
  double approcheAvecFreinDistanceMm = 10;
  double approcheAvecFreinOrientationDeg = 5;
  double approcheSansFreinDistanceMm = 50;
  double approcheSansFreinOrientationDeg = 5;
  double startAngleDemiTourDeg = 75;
  double startAngleLimitVitesseDistance = 15;

  // -------------------------- //
  // Paramètre Physiques        //
  // -------------------------- //

  double dstCallageCote = 56; // coté du robot <=> milieu du robot
  double dstCallageArriere = 46.5; // dos du robot <=> milieu du robot

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
  int pathFindingAngle = 30;
  int pathFindingAngleSafe = 40;
  int pathFindingSeuilProximite = 160;
  int pathFindingSeuilProximiteSafe = 200;
  int pathFindingSeuilProximiteArig = 140;
  int pathFindingTailleObstacle = PamiConstantesConfig.pathFindingSeuilProximite * 2 + 50;
  int pathFindingTailleObstacleArig = PamiConstantesConfig.pathFindingSeuilProximiteArig * 2 + 50;

  int lidarClusterSizeMm = 50;
  int lidarOffsetPointMm = 0; // "recule" les points détectés pour prendre en compte qu'on ne detecte que les faces avant

  // ----------------- //
  // Paramètres métier //
  // ----------------- //

  int WAIT_LED = 200;
  int TIMEOUT_POMPE = 1000;
  int TIMEOUT_COLOR = 800;
}
