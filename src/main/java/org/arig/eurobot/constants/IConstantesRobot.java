package org.arig.eurobot.constants;

/**
 * Created by gdepuille on 23/12/14.
 */
public interface IConstantesRobot {

    // Configuration asservissement //
    static final double asservTimeMs = 10;

    // Durée du match //
    static final int matchTimeMs = 90000;

    // ----------------------------- //
    // Configuration des convertions //
    // ----------------------------- //

    // 40000 p => 1524.5 mm : 40000 / 1524.5 = 26,238110856
    static final double countPerMm = 26.238110856;

    // 51325 p => 360° : 51325 / 360 = 142,569444444
    static final double countPerDeg = 142.569444444;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    static final double rampAccDistance = 500.0; // en mm/s2
    static final double rampDecDistance = 500.0; // en mm/s2

    static final double rampAccOrientation = 800.0; // en mm/s2
    static final double rampDecOrientation = 800.0; // en mm/s2

    // -------------- //
    // Parametres PID //
    // -------------- //

//    static final double kpDistance = 0.75;
//    static final double kiDistance = 18.75;
//    static final double kdDistance = 0.0;
    static final double kpDistance = 0.65;
    static final double kiDistance = 4.0;
    static final double kdDistance = 0.00050;

    static final double kpOrientation = 0.15;
    static final double kiOrientation = 3.5;
    static final double kdOrientation = 0.0050;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    static final double arretDistanceMm = 1;
    static final double arretOrientDeg = 1;
    static final double approcheDistanceMm = 10;
    static final double approcheOrientationDeg = 5;
    static final double angleReculDeg = 0.45;

    // ----------------- //
    // Paramètres métier //
    // ----------------- //

    static final int nbPiedMax = 4;
}
