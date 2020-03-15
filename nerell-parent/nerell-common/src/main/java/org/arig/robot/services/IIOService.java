package org.arig.robot.services;

public interface IIOService {

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    void refreshAllPcf();
    boolean auOk();
    boolean alimPuissance5VOk();
    boolean alimPuissance12VOk();
    boolean tirette();

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean presencePinceAvant1();
    boolean presencePinceAvant2();
    boolean presencePinceAvant3();
    boolean presencePinceAvant4();

    boolean presencePinceArriere1();
    boolean presencePinceArriere2();
    boolean presencePinceArriere3();
    boolean presencePinceArriere4();
    boolean presencePinceArriere5();

    boolean calageBordureArriereDroit();
    boolean calageBordureArriereGauche();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableAlim5VPuissance();
    void disableAlim5VPuissance();
    void enableAlim12VPuissance();
    void disableAlim12VPuissance();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}
