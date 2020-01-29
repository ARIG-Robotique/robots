package org.arig.robot.services;

import org.arig.robot.model.EStrategy;
import org.arig.robot.model.Team;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;

import java.util.List;

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
    boolean presenceLectureCouleur();

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

    // Analogique

    // Couleur
    TCS34725ColorSensor.ColorData couleurRaw();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableLedCapteurCouleur();
    void disableLedCapteurCouleur();

    void enableMoteurDrapeau();
    void disableMoteurDrapeau();

    void enableAlim5VPuissance();
    void disableAlim5VPuissance();
    void enableAlim12VPuissance();
    void disableAlim12VPuissance();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}
