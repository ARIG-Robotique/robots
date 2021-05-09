package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;

public interface IOdinIOService extends IIOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean presenceVentouseAvant1();
    boolean presenceVentouseAvant2();
    boolean presenceVentouseArriere1();
    boolean presenceVentouseArriere2();

    // Analogique
    ECouleurBouee couleurBoueeAvant1();
    ECouleurBouee couleurBoueeAvant2();
    ECouleurBouee couleurBoueeArriere1();
    ECouleurBouee couleurBoueeArriere2();

    ColorData couleurRawAvant1();
    ColorData couleurRawAvant2();
    ColorData couleurRawArriere1();
    ColorData couleurRawArriere2();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableLedCapteurCouleur();
    void disableLedCapteurCouleur();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

    void enablePompeAvant1();
    void enablePompeAvant2();
    void enablePompeArriere1();
    void enablePompeArriere2();

    void disablePompeAvant1();
    void disablePompeAvant2();
    void disablePompeArriere1();
    void disablePompeArriere2();

}
