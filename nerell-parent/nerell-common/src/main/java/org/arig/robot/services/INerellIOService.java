package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;

public interface INerellIOService extends IIOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean presenceVentouse1();
    boolean presenceVentouse2();
    boolean presenceVentouse3();
    boolean presenceVentouse4();

    boolean presencePinceArriere1();
    boolean presencePinceArriere2();
    boolean presencePinceArriere3();
    boolean presencePinceArriere4();
    boolean presencePinceArriere5();

    // Analogique
    ECouleurBouee couleurBouee1();
    ECouleurBouee couleurBouee2();
    ECouleurBouee couleurBouee3();
    ECouleurBouee couleurBouee4();

    ColorData couleurRaw1();
    ColorData couleurRaw2();
    ColorData couleurRaw3();
    ColorData couleurRaw4();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableLedCapteurCouleur();
    void disableLedCapteurCouleur();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

    void enablePompe1();
    void enablePompe2();
    void enablePompe3();
    void enablePompe4();

    void disablePompe1();
    void disablePompe2();
    void disablePompe3();
    void disablePompe4();

}
