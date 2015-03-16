package org.arig.prehistobot.constants;

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
    static final double countPerMm = 15.9954716675272;
    static final double countPerDeg = 44.9469570072585;

    // ------------------------ //
    // Configuration des rampes //
    // ------------------------ //
    static final double rampAccDistance = 50.0; // en mm/s2
    static final double rampDecDistance = 50.0; // en mm/s2

    static final double rampAccOrientation = 50.0; // en mm/s2
    static final double rampDecOrientation = 50.0; // en mm/s2

    // -------------- //
    // Parametres PID //
    // -------------- //
    /*
    static final double kpDistance = 0.30;
    static final double kiDistance = 0.20;
    static final double kdDistance = 0.90;

    static final double kpOrientation = 1.00;
    static final double kiOrientation = 0.10;
    static final double kdOrientation = 1.00;
    */

    static final double kpDistance = 0.5;
    static final double kiDistance = 0.0;
    static final double kdDistance = 0.0;

    static final double kpOrientation = 0.5;
    static final double kiOrientation = 0.0;
    static final double kdOrientation = 0.0;


    // --------------------------- //
    // Paramètre mouvement manager //
    // --------------------------- //
    static final double arretDistanceMm = 1;
    static final double arretOrientDeg = 1;
    static final double angleReculDeg = 95;
}
