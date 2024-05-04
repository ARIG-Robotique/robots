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

    boolean presenceStockGauche(boolean expectedSimulator);
    boolean presenceStockCentre(boolean expectedSimulator);
    boolean presenceStockDroite(boolean expectedSimulator);

    boolean inductifGauche();
    boolean inductifDroit();

    boolean inductifGaucheAverage();
    boolean inductifDroitAverage();

    boolean stockGaucheAverage(boolean expectedSimulateur);
    boolean stockCentreAverage(boolean expectedSimulateur);
    boolean stockDroiteAverage(boolean expectedSimulateur);

    boolean pinceAvantGaucheAverage(boolean expectedSimulateur);
    boolean pinceAvantCentreAverage(boolean expectedSimulateur);
    boolean pinceAvantDroiteAverage(boolean expectedSimulateur);

    boolean pinceArriereGaucheAverage(boolean expectedSimulateur);
    boolean pinceArriereCentreAverage(boolean expectedSimulateur);
    boolean pinceArriereDroiteAverage(boolean expectedSimulateur);

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
