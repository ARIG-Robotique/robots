package org.arig.robot.services;

public interface CommonRobotIOService extends IOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean pinceAvantGauche();
    boolean pinceAvantCentre();
    boolean pinceAvantDroite();
    boolean pinceArriereGauche();
    boolean pinceArriereCentre();
    boolean pinceArriereDroite();

    boolean presenceAvantGauche();
    boolean presenceAvantCentre();
    boolean presenceAvantDroite();
    boolean presenceArriereGauche();
    boolean presenceArriereCentre();
    boolean presenceArriereDroite();

    boolean inductifGauche();
    boolean inductifDroit();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //


    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //


}
