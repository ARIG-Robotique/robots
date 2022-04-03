package org.arig.robot.services;

import org.arig.robot.model.CouleurEchantillon;

public interface NerellIOService extends IOService {

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
    boolean presenceVentouseBas();
    boolean presenceVentouseHaut();

    boolean presenceCarreFouille();
    boolean presencePriseBras();
    boolean presenceStock1();
    boolean presenceStock2();
    boolean presenceStock3();
    boolean presenceStock4();
    boolean presenceStock5();
    boolean presenceStock6();

    // Couleurs
    CouleurEchantillon couleurVentouseBas();
    CouleurEchantillon couleurVentouseHaut();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableLedCapteurCouleur();
    void disableLedCapteurCouleur();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

    void disableAllPompes();

    void enableForceAllPompes();
    void enableForcePompeVentouseBas();
    void enableForcePompeVentouseHaut();

    void enableAllPompes();
    void enablePompeVentouseBas();
    void enablePompeVentouseHaut();

    void releaseAllPompes();
    void releasePompeVentouseBas();
    void releasePompeVentouseHaut();

}
