package org.arig.robot.services;

public interface CommonRobotIOService extends IOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean pinceAvantGauche(boolean expectedSimulator);
    boolean pinceAvantCentre(boolean expectedSimulator);
    boolean pinceAvantDroite(boolean expectedSimulator);
    boolean pinceArriereGauche(boolean expectedSimulator);
    boolean pinceArriereCentre(boolean expectedSimulator);
    boolean pinceArriereDroite(boolean expectedSimulator);

    boolean presenceAvantGauche(boolean expectedSimulator);
    boolean presenceAvantCentre(boolean expectedSimulator);
    boolean presenceAvantDroite(boolean expectedSimulator);
    boolean presenceArriereGauche(boolean expectedSimulator);
    boolean presenceArriereCentre(boolean expectedSimulator);
    boolean presenceArriereDroite(boolean expectedSimulator);

    boolean presenceStockGauche();
    boolean presenceStockCentre();
    boolean presenceStockDroite();

    boolean inductifGauche();
    boolean inductifDroit();

    // Analogique

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableElectroAimant();
    void disableElectroAimant();

    void tournePanneauJaune();
    void tournePanneauJaune(int speed);
    void tournePanneauBleu();
    void tournePanneauBleu(int speed);
    void stopTournePanneau();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //


}
