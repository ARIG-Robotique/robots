package org.arig.robot.services;

public interface CommonRobotIOService extends IOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean pinceAvantGauche(boolean expectedSimulator);
    boolean pinceAvantDroite(boolean expectedSimulator);
    boolean pinceArriereGauche(boolean expectedSimulator);
    boolean pinceArriereDroite(boolean expectedSimulator);

    boolean solAvantGauche(boolean expectedSimulator);
    boolean solAvantDroite(boolean expectedSimulator);
    boolean solArriereGauche(boolean expectedSimulator);
    boolean solArriereDroite(boolean expectedSimulator);

    boolean tiroirAvantHaut(boolean expectedSimulator);
    boolean tiroirAvantBas(boolean expectedSimulator);
    boolean tiroirArriereHaut(boolean expectedSimulator);
    boolean tiroirArriereBas(boolean expectedSimulator);

    // Analogique

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //


    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //


}
