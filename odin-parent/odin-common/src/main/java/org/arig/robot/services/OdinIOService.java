package org.arig.robot.services;

import org.arig.robot.model.Couleur;

public interface OdinIOService extends IOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    @Override
    default boolean puissanceServosOk() {
        return true;
    }

    @Override
    default boolean puissanceMoteursOk() {
        return true;
    }

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
    Couleur couleurAvantGauche();
    Couleur couleurAvantDroit();
    Couleur couleurArriereGauche();
    Couleur couleurArriereDroit();

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
