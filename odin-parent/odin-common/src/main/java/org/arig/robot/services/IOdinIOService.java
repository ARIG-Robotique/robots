package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public interface IOdinIOService extends IIOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean presenceVentouseAvantGauche();
    boolean presenceVentouseAvantDroit();
    boolean presenceVentouseArriereGauche();
    boolean presenceVentouseArriereDroit();

    // Analogique
    ECouleurBouee couleurBoueeAvantGauche();
    ECouleurBouee couleurBoueeAvantDroit();
    ECouleurBouee couleurBoueeArriereGauche();
    ECouleurBouee couleurBoueeArriereDroit();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableLedCapteurCouleur();
    void disableLedCapteurCouleur();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //
    void disableAllPompe();

    void enableAllPompe();
    void enablePompeAvantGauche();
    void enablePompeAvantDroit();
    void enablePompeArriereGauche();
    void enablePompeArriereDroit();

    void releaseAllPompe();
    void releasePompeAvantGauche();
    void releasePompeAvantDroit();
    void releasePompeArriereGauche();
    void releasePompeArriereDroit();

}
