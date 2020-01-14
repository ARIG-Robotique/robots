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
    boolean ledCapteurCouleur();
    boolean presenceVentouseAvant();
    boolean calageBordureArriereDroit();
    boolean calageBordureArriereGauche();
    boolean presenceLectureCouleur();

    // Analogique
    boolean gobeletPritDansVentouseAvant();

    // Couleur
    TCS34725ColorSensor.ColorData couleurRaw();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableLedCapteurCouleur();
    void disableLedCapteurCouleur();

    void enableAlim5VPuissance();
    void disableAlim5VPuissance();
    void enableAlim12VPuissance();
    void disableAlim12VPuissance();

    void airElectroVanneAvant();
    void videElectroVanneAvant();

    void enablePompeAVideAvant();
    void disablePompeAVideAvant();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}
