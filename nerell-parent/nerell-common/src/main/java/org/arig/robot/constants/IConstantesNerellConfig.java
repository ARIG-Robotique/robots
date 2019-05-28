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
    double asservTimeCarouselMs = 50;
    double asservTimeCarouselS = IConstantesNerellConfig.asservTimeCarouselMs / 1000;

    double i2cReadTimeMs = 50;

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

    // Carousel
    long countPerCarouselIndex = 1305;
    long countOffsetInitCarousel = 1350;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    double rampAccDistance = 800.0; // en mm/s2
    double rampDecDistance = 500.0; // en mm/s2

    double rampAccOrientation = 800.0; // en mm/s2
    double rampDecOrientation = 500.0; // en mm/s2

    double rampAccCarousel = 800.0; // en mm/s2
    double rampDecCarousel = 500.0; // en mm/s2

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
    long vitesseMouvement = vitesseLente;
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

    double kpCarousel = 10;
    double kiCarousel = 0.5 * asservTimeCarouselS;
    double kdCarousel = 0.000625 / asservTimeCarouselS;

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
    double dstVentouseFacade = 180; // distance de la ventouse en prise facade au milieu de roues
    double dstAtomeCentre = 50; // distance du milieu du palet au centre du robot (largeur)
    double dstAtomeCentre2 = 120; // distance du milieu du palet au centre du robot (longueur)
    int dstTinylidarAvant = 50; // FIXME

    double seuilErreurPidOrientation = 30000;
    double seuilErreurPidDistance = 30000;

    // -------------------------- //
    // Paramètre Carousel manager //
    // -------------------------- //
    double arretCarouselPulse = 5;

    // -------------------------- //
    // Paramètre Avoiding service //
    // -------------------------- //
    enum AvoidingSelection {
        BASIC, FULL, NOT_BASIC
    }

    // ---------------------- //
    // Paramètre path finding //
    // ---------------------- //
    PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.LAZY_THETA_STAR;
    int pathFindingAngle = 45;
    int pathFindingSeuilAvoidance = 900;
    int pathFindingSeuilProximite = 600;
    int pathFindingTailleObstacle = IConstantesNerellConfig.pathFindingSeuilProximite + 50;

    // --------------------- //
    // Paramètre pneumatique //
    // --------------------- //
    int tempsActivationElectrovanne = 200;

    // ----------------- //
    // Paramètres métier //
    // ----------------- //
    int offsetDetectionPaletMagasin = 0;
    int diametrePaletMm = 76;

    int nbPaletsBalanceMax = 6;
    int nbPaletsAccelerateurMax = 10;
    int nbPaletsMagasinMax = 3;
    int offsetTableau = 80;

}
