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

    boolean presenceAvantGauche();
    boolean presenceAvantDroit();
    boolean presenceArriereGauche();
    boolean presenceArriereDroit();

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
    void enablePompesAvant();
    void enablePompesArriere();
    void enablePompeAvantGauche();
    void enablePompeAvantDroit();
    void enablePompeArriereGauche();
    void enablePompeArriereDroit();

    void releaseAllPompe();
    void releasePompesAvant();
    void releasePompesArriere();
    void releasePompeAvantGauche();
    void releasePompeAvantDroit();
    void releasePompeArriereGauche();
    void releasePompeArriereDroit();

}
