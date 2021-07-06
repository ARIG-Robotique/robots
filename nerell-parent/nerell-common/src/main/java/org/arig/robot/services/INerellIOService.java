package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public interface INerellIOService extends IIOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean presenceVentouse1();
    boolean presenceVentouse2();
    boolean presenceVentouse3();
    boolean presenceVentouse4();

    boolean presence1();
    boolean presence2();
    boolean presence3();
    boolean presence4();

    boolean presencePinceArriere1();
    boolean presencePinceArriere2();
    boolean presencePinceArriere3();
    boolean presencePinceArriere4();
    boolean presencePinceArriere5();

    // Couleurs
    ECouleurBouee couleurBoueeAvant1();
    ECouleurBouee couleurBoueeAvant2();
    ECouleurBouee couleurBoueeAvant3();
    ECouleurBouee couleurBoueeAvant4();

    ECouleurBouee couleurBoueeArriere2();
    ECouleurBouee couleurBoueeArriere4();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableLedCapteurCouleur();
    void disableLedCapteurCouleur();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

    void disableAllPompes();

    void enableAllPompes();
    void enablePompe1();
    void enablePompe2();
    void enablePompe3();
    void enablePompe4();

    void releaseAllPompes();
    void releasePompe1();
    void releasePompe2();
    void releasePompe3();
    void releasePompe4();

}
