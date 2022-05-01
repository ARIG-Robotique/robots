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
    default boolean calageAvantBasDroit() {
        return false;
    }
    default boolean calageAvantBasGauche() {
        return false;
    }
    default boolean calageAvantHautDroit() {
        return false;
    }
    default boolean calageAvantHautGauche() {
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
