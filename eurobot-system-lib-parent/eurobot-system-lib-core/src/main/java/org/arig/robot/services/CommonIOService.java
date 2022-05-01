package org.arig.robot.services;

import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;

public interface CommonIOService extends IOService {

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean presenceVentouseBas();
    boolean presenceVentouseHaut();

    boolean presenceStatuette();
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

    TCS34725ColorSensor.ColorData couleurVentouseHautRaw();
    TCS34725ColorSensor.ColorData couleurVentouseBasRaw();

    default CouleurEchantillon computeCouleur(TCS34725ColorSensor.ColorData c) {
        if (c.c() < 3000) {
            return CouleurEchantillon.INCONNU;
        }

        double r2g = 1. * c.r() / c.g();
        double r2b = 1. * c.r() / c.b();
        double g2r = 1. / r2g;
        double g2b = 1. * c.g() / c.b();
        double b2r = 1. / r2b;
        double b2g = 1. / g2b;

        if (r2g > 1.2 && r2b > 1.2) {
            return CouleurEchantillon.ROUGE;
        }
        if (g2r > 1.2 && g2b > 1.2) {
            return CouleurEchantillon.VERT;
        }
        if (b2r > 1.2 && b2g > 0.9 && b2g < 1.1 && g2r > 1.2) {
            return CouleurEchantillon.BLEU;
        }
        return CouleurEchantillon.ROCHER;
    }

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
