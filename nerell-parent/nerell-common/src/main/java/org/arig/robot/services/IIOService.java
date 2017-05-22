package org.arig.robot.services;

import org.arig.robot.model.Team;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;

/**
 * @author gdepuille on 23/04/15.
 */
public interface IIOService {

    Team equipe();

    boolean auOk();
    boolean alimPuissance5VOk();
    boolean alimPuissance12VOk();
    boolean tirette();
    boolean bordureAvant();
    boolean bordureArriereDroite();
    boolean bordureArriereGauche();
    boolean presenceEntreeMagasin();
    boolean presenceDevidoir();
    boolean presencePinceDroite();
    boolean presencePinceCentre();
    boolean presenceBaseLunaireDroite();
    boolean presenceBaseLunaireGauche();
    boolean presenceBallesAspiration();
    boolean presenceRouleaux();
    boolean presenceFusee();
    boolean finCourseGlissiereDroite();
    boolean finCourseGlissiereGauche();
    boolean presenceModuleDansBras();

    boolean ledCapteurCouleur();
    ColorData frontColor();
    Team getTeamColorFromSensor();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void colorLedRGBKo();
    void colorLedRGBOk();
    void teamColorLedRGB();
    void clearColorLedRGB();

    void enableLedCapteurCouleur();
    void disableLedCapteurCouleur();

    void enableAlim5VPuissance();
    void disableAlim5VPuissance();
    void enableAlim12VPuissance();
    void disableAlim12VPuissance();

    void enableElectroVanne();
    void disableElectroVanne();
    void enablePompeAVide();
    void disablePompeAVide();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

    boolean glissiereOuverte();
    boolean glissiereFerme();
}
