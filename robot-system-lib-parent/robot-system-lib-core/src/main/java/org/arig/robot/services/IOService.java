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
    boolean calageArriereDroit();
    boolean calageArriereGauche();
    boolean calageAvantBasDroit();
    boolean calageAvantBasGauche();
    boolean calageAvantHautDroit();
    boolean calageAvantHautGauche();
    boolean calageLatteralDroit();
    boolean calagePriseEchantillon();
    boolean calageVentouseBas();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableAlimServos();
    void disableAlimServos();
    void enableAlimMoteurs();
    void disableAlimMoteurs();

}
