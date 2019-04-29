package org.arig.robot.services;

import lombok.Setter;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 30/10/16.
 */
@Service
public class IOServiceBouchon implements IIOService {

    @Autowired private RobotStatus rs;

    @Setter private Team team = Team.UNKNOWN;
    @Setter private boolean au = false;
    @Setter private boolean tirette = false;
    private boolean ledCapteurCouleur = false;
    private boolean alim5V = false;
    private boolean alim12V = false;

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public Team equipe() {
        rs.setTeam(team);
        return rs.getTeam();
    }

    @Override
    public boolean auOk() {
        return au;
    }

    @Override
    public boolean alimPuissance5VOk() {
        return alim5V;
    }

    @Override
    public boolean alimPuissance12VOk() {
        return alim12V;
    }

    @Override
    public boolean tirette() {
        return tirette;
    }

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique

    @Override
    public boolean ledCapteurCouleur() {
        return ledCapteurCouleur;
    }

    @Override
    public boolean presencePaletDroit() {
        return false;
    }

    @Override
    public boolean presencePaletGauche() {
        return false;
    }

    @Override
    public boolean buteePaletDroit() {
        return false;
    }

    @Override
    public boolean buteePaletGauche() {
        return false;
    }

    @Override
    public boolean presencePaletVentouseDroit() {
        return false;
    }

    @Override
    public boolean presencePaletVentouseGauche() {
        return false;
    }

    @Override
    public boolean calageBordureArriereDroit() {
        return false;
    }

    @Override
    public boolean calageBordureArriereGauche() {
        return false;
    }

    @Override
    public boolean trappeMagasinDroitFerme() {
        return false;
    }

    @Override
    public boolean trappeMagasinGaucheFerme() {
        return false;
    }

    @Override
    public boolean indexBarillet() {
        return false;
    }

    @Override
    public boolean presenceLectureCouleur() {
        return false;
    }

    // Analogique

    @Override
    public boolean paletPrisDansVentouseDroit() {
        return false;
    }

    @Override
    public boolean paletPrisDansVentouseGauche() {
        return false;
    }

    @Override
    public byte nbPaletDansMagasinDroit() {
        return 0;
    }

    @Override
    public byte nbPaletDansMagasinGauche() {
        return 0;
    }

    @Override
    public int distanceTelemetreAvantDroit() {
        return 0;
    }

    @Override
    public int distanceTelemetreAvantGauche() {
        return 0;
    }


    // Couleur


    @Override
    public TCS34725ColorSensor.ColorData couleurPaletRaw() {
        return new TCS34725ColorSensor.ColorData().r(0).g(0).b(0);
    }

    @Override
    public CouleurPalet couleurPalet() {
        return CouleurPalet.INCONNU;
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void colorLedRGBKo() { }

    @Override
    public void colorLedRGBOk() { }

    @Override
    public void teamColorLedRGB() { }

    @Override
    public void clearColorLedRGB() { }

    @Override
    public void enableLedCapteurCouleur() {
        ledCapteurCouleur = true;
    }

    @Override
    public void disableLedCapteurCouleur() {
        ledCapteurCouleur = false;
    }

    @Override
    public void enableAlim5VPuissance() {
        alim5V = true;
    }

    @Override
    public void disableAlim5VPuissance() {
        alim5V = false;
    }

    @Override
    public void enableAlim12VPuissance() {
        alim12V = true;
    }

    @Override
    public void disableAlim12VPuissance() {
        alim12V = false;
    }

    @Override
    public void airElectroVanneDroite() { }

    @Override
    public void videElectroVanneDroite() { }

    @Override
    public void airElectroVanneGauche() { }

    @Override
    public void videElectroVanneGauche() { }

    @Override
    public void enablePompeAVideDroite() { }

    @Override
    public void disablePompeAVideDroite() { }

    @Override
    public void enablePompeAVideGauche() { }

    @Override
    public void disablePompeAVideGauche() { }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}
