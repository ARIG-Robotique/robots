package org.arig.robot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

public interface IConstantesNerellConfig {

    // Nb Thread Pool Scheduler
    int nbThreadScheduledExecutor = 11;

    // Nb Thread Pool Async
    int nbThreadAsyncExecutor = 10;

    // Configuration asservissement //
    double asservTimeMs = 10;
    double asservTimeCarouselMs = 50;

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
    long countPerCarouselIndex = 1;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    double rampAccDistance = 250.0; // en mm/s2
    double rampDecDistance = 250.0; // en mm/s2

    double rampAccOrientation = 1000.0; // en mm/s2
    double rampDecOrientation = 1000.0; // en mm/s2

    double rampAccCarousel = 100.0; // en mm/s2
    double rampDecCarousel = 100.0; // en mm/s2

    // -------------------------- //
    // Configuration des vitesses //
    // -------------------------- //

    long vitesseOrientation = 800;
    long vitesseOrientationBasse = 300;

    long vitesseSuperHaute = 750;
    long vitesseHaute = 600;
    long vitesseMoyenneHaute = 500;
    long vitesseMoyenneBasse = 400;
    long vitesseLente = 300;
    long vitesseSuperLente = 200;

    long vitessePath = vitesseLente;
    long vitesseMouvement = vitesseLente;

    // -------------- //
    // Parametres PID //
    // -------------- //

    double kpDistance = 0.2;
    double kiDistance = 0.05;
    double kdDistance = 0.01;

    double kpOrientation = 0.2;
    double kiOrientation = 0.05;
    double kdOrientation = 0.01;

    double kpMotDroit = 0.9;
    double kiMotDroit = 0.005;
    double kdMotDroit = 0.09;

    double kpMotGauche = 0.9;
    double kiMotGauche = 0.005;
    double kdMotGauche = 0.09;

    double kpCarousel = 0.1;
    double kiCarousel = 0;
    double kdCarousel = 0;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    double arretDistanceMm = 1;
    double arretOrientDeg = 1;
    double approcheDistanceMm = 100;
    double approcheOrientationDeg = 5;
    double angleReculDeg = 0.45;

    // -------------------------- //
    // Paramètre Carousel manager //
    // -------------------------- //
    double arretCarouselPulse = 5;

    // -------------------------- //
    // Paramètre Avoiding service //
    // -------------------------- //
    enum AvoidingSelection {
        BASIC, FULL
    }

    // ---------------------- //
    // Paramètre path finding //
    // ---------------------- //
    PathFinderAlgorithm pathFindingAlgo = PathFinderAlgorithm.LAZY_THETA_STAR;

    // --------------------- //
    // Paramètre pneumatique //
    // --------------------- //
    int tempsActivationElectrovanne = 100;

    // ----------------- //
    // Paramètres métier //
    // ----------------- //
    int offsetDetectionPaletMagasin = 0;
    int diametrePaletMm = 76;

    int nbPaletsBalanceMax = 6;
    int nbPaletsAccelerateurMax = 9; // TODO à valider
    int nbPaletsMagasinMax = 3;
    int dstPinceCentre = 100; // TODO à valider
    int offsetTableau = 100; // TODO

}
