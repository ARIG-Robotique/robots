package org.arig.robot.services;

public interface IOService {

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    void refreshAllIO();
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
    boolean calagePrisePotArriere();
    boolean calagePrisePotArriere(int mandatorySensors);

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableAlimServos();
    void disableAlimServos();
    void enableAlimMoteurs();
    void disableAlimMoteurs();

}
