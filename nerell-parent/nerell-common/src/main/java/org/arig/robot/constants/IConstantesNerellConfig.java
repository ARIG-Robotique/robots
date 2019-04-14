package org.arig.robot.constants;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;

/**
 * @author gdepuille on 29/04/15.
 */
public interface IConstantesNerellConfig {

    // Nb Thread Pool Scheduler
    int nbThreadScheduledExecutor = 10;

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
    double rampAccDistance = 1000.0; // en mm/s2
    double rampDecDistance = 500.0; // en mm/s2

    double rampAccOrientation = 1000.0; // en mm/s2
    double rampDecOrientation = 1000.0; // en mm/s2

    double rampAccCarousel = 1000.0; // en mm/s2
    double rampDecCarousel = 1000.0; // en mm/s2

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

    long vitessePath = vitesseSuperLente;
    long vitesseMouvement = vitesseLente;

    // -------------- //
    // Parametres PID //
    // -------------- //

    double kpDistance = 1.5;
    double kiDistance = 6.0;
    double kdDistance = 0.0050;

    double kpOrientation = 0.5;
    double kiOrientation = 5.5;
    double kdOrientation = 0.050;

    double kpCarousel = 1;
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

    // ----------------- //
    // Paramètres métier //
    // ----------------- //
    int nbPaletsBalanceMax = 6;
    int nbPaletsAccelerateurMax = 9; // TODO à valider
    int nbPaletsMagasinMax = 3;
    int dstPinceCentre = 100; // TODO à valider

}
