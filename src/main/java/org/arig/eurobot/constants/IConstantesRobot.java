package org.arig.eurobot.constants;

/**
 * Created by gdepuille on 23/12/14.
 */
public interface IConstantesRobot {

    // Configuration asservissement //
    static final double asservTimeMs = 10;

    // Durée du match //
    static final int matchTimeMs = 60000;

    // ----------------------------- //
    // Configuration des convertions //
    // ----------------------------- //
    static final double countPerMm = 1;
    static final double countPerDeg = 1;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    static final double rampAccDistance = 500.0; // en mm/s2
    static final double rampDecDistance = 500.0; // en mm/s2

    static final double rampAccOrientation = 500.0; // en mm/s2
    static final double rampDecOrientation = 500.0; // en mm/s2

    // -------------- //
    // Parametres PID //
    // -------------- //

    static final double kpDistance = 1.00;
    static final double kiDistance = 1.00;
    static final double kdDistance = 1.00;

    static final double kpOrientation = 1.00;
    static final double kiOrientation = 1.00;
    static final double kdOrientation = 1.00;

    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    static final double arretDistanceMm = 1;
    static final double arretOrientDeg = 1;
    static final double angleReculDeg = 95;
}
