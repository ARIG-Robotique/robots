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
    default boolean calageArriereDroit() {
        return false;
    }
    default boolean calageArriereGauche() {
        return false;
    }
    default boolean calageAvantDroit() {
        return false;
    }
    default boolean calageAvantGauche() {
        return false;
    }

    default boolean calageLatteralDroit() {
        return true;
    }

    default boolean calagePriseEchantillon() {
        return false;
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableAlimServos();
    void disableAlimServos();
    void enableAlimMoteurs();
    void disableAlimMoteurs();

}
