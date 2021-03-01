package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;

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
    @Deprecated
    boolean presencePinceAvantLat1();
    @Deprecated
    boolean presencePinceAvantLat2();
    @Deprecated
    boolean presencePinceAvantLat3();
    @Deprecated
    boolean presencePinceAvantLat4();

    boolean presenceVentouse1();
    boolean presenceVentouse2();
    boolean presenceVentouse3();
    boolean presenceVentouse4();

    boolean presencePinceAvantSup1();
    boolean presencePinceAvantSup2();
    boolean presencePinceAvantSup3();
    boolean presencePinceAvantSup4();

    boolean presencePinceArriere1();
    boolean presencePinceArriere2();
    boolean presencePinceArriere3();
    boolean presencePinceArriere4();
    boolean presencePinceArriere5();

    boolean calageBordureDroit();
    boolean calageBordureGauche();

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

    void enableAlim5VPuissance();
    void disableAlim5VPuissance();
    void enableAlim12VPuissance();
    void disableAlim12VPuissance();

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
