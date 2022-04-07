package org.arig.robot.services;

import org.arig.robot.model.CouleurEchantillon;

public interface CommonIOService extends IOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean presenceVentouseBas();
    boolean presenceVentouseHaut();

    boolean presenceCarreFouille(boolean expected);
    boolean presencePriseBras();
    boolean presenceStock1();
    boolean presenceStock2();
    boolean presenceStock3();
    boolean presenceStock4();
    boolean presenceStock5();
    boolean presenceStock6();

    default boolean presenceStock(int indexStock) {
        switch (indexStock) {
            case 0: return presenceStock1();
            case 1: return presenceStock2();
            case 2: return presenceStock3();
            case 3: return presenceStock4();
            case 4: return presenceStock5();
            case 5: return presenceStock6();
            default: return false;
        }
    }

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
