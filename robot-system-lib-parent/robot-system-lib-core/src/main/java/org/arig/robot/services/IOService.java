package org.arig.robot.services;

public interface IOService {

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    default void refreshAllIO() {}
    boolean auOk();
    boolean puissanceServosOk();
    boolean puissanceMoteursOk();
    boolean tirette();

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean calageAvantGauche();
    boolean calageAvantDroit();
    boolean calageArriereGauche();
    boolean calageArriereDroit();

    boolean calagePriseProduitAvant();
    boolean calagePriseProduitAvant(int mandatorySensors);
    boolean calagePriseProduitArriere();
    boolean calagePriseProduitArriere(int mandatorySensors);
    boolean calageElectroaimant();
    boolean calageElectroaimant(int mandatorySensors);

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableAlimServos();
    void disableAlimServos();
    void enableAlimMoteurs();
    void disableAlimMoteurs();

}
