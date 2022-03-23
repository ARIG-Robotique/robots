package org.arig.robot.services;

public interface IIOService {

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
    default boolean calageBordureArriereDroit() {
        return false;
    }
    default boolean calageBordureArriereGauche() {
        return false;
    }
    default boolean calageBordureAvantDroit() {
        return false;
    }
    default boolean calageBordureAvantGauche() {
        return false;
    }

    default boolean calageBordureCustomDroit() {
        return false;
    }
    default boolean calageBordureCustomGauche(){
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
