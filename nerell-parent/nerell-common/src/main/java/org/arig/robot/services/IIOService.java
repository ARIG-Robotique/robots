package org.arig.robot.services;

import org.arig.robot.model.Palet.Couleur;
import org.arig.robot.model.Team;

/**
 * @author gdepuille on 23/04/15.
 */
public interface IIOService {

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //
    Team equipe();

    boolean auOk();
    boolean alimPuissance5VOk();
    boolean alimPuissance12VOk();
    boolean tirette();

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean ledCapteurCouleur();
    boolean presencePaletDansRobotDroit();
    boolean presencePaletDansRobotGauche();
    boolean buteePaletDroit();
    boolean buteePaletGauche();
    boolean presencePaletVentouseDroit();
    boolean presencePaletVentouseGauche();
    boolean calageBordureArriereDroit();
    boolean calageBordureArriereGauche();
    boolean trappeMagasinDroitFerme();
    boolean trappeMagasinGaucheFerme();
    boolean indexBarillet();
    boolean presenceLectureCouleur();

    // Analogique
    boolean paletPrisDansVentouseDroit();
    boolean paletPrisDansVentouseGauche();
    byte nbPaletDansMagasinDroit();
    byte nbPaletDansMagasinGauche();
    int distanceTelemetreAvantDroit();
    int distanceTelemetreAvantGauche();

    // Couleur
    Couleur couleurPalet();

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

    void enablePompeAVideDroite();
    void disablePompeAVideDroite();
    void enablePompeAVideGauche();
    void disablePompeAVideGauche();

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}
