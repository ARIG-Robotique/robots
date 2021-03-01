package org.arig.robot.services;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.springframework.stereotype.Service;

@Service("IOService")
public class IOServiceBouchon implements IIOService {

    @Setter
    @Accessors(fluent = true)
    private boolean au = false;

    @Setter
    @Accessors(fluent = true)
    private boolean tirette = false;

    private boolean alim5V = false;
    private boolean alim12V = false;

    @Setter
    @Accessors(fluent = true)
    private boolean contentPinceArriere = false;

    @Setter
    @Accessors(fluent = true)
    private boolean contentPinceAvantLat1 = false;

    @Setter
    @Accessors(fluent = true)
    private boolean contentPinceAvantLat2 = false;

    @Setter
    @Accessors(fluent = true)
    private boolean contentPinceAvantLat3 = false;

    @Setter
    @Accessors(fluent = true)
    private boolean contentPinceAvantLat4 = false;

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public void refreshAllPcf() {
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
    public boolean presencePinceAvantLat1() {
        return contentPinceAvantLat1;
    }

    @Override
    public boolean presencePinceAvantLat2() {
        return contentPinceAvantLat2;
    }

    @Override
    public boolean presencePinceAvantLat3() {
        return contentPinceAvantLat3;
    }

    @Override
    public boolean presencePinceAvantLat4() {
        return contentPinceAvantLat4;
    }

    @Override
    public boolean presenceVentouse1() {
        return false;
    }

    @Override
    public boolean presenceVentouse2() {
        return false;
    }

    @Override
    public boolean presenceVentouse3() {
        return false;
    }

    @Override
    public boolean presenceVentouse4() {
        return false;
    }

    @Override
    public boolean presencePinceAvantSup1() {
        return false;
    }

    @Override
    public boolean presencePinceAvantSup2() {
        return false;
    }

    @Override
    public boolean presencePinceAvantSup3() {
        return false;
    }

    @Override
    public boolean presencePinceAvantSup4() {
        return false;
    }

    @Override
    public boolean presencePinceArriere1() {
        return contentPinceArriere;
    }

    @Override
    public boolean presencePinceArriere2() {
        return contentPinceArriere;
    }

    @Override
    public boolean presencePinceArriere3() {
        return contentPinceArriere;
    }

    @Override
    public boolean presencePinceArriere4() {
        return contentPinceArriere;
    }

    @Override
    public boolean presencePinceArriere5() {
        return contentPinceArriere;
    }

    @Override
    public boolean calageBordureDroit() {
        return false;
    }

    @Override
    public boolean calageBordureGauche() {
        return false;
    }

    @Override
    public ECouleurBouee couleurBouee1() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ECouleurBouee couleurBouee2() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ECouleurBouee couleurBouee3() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ECouleurBouee couleurBouee4() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ColorData couleurRaw1() {
        return new ColorData().r(0).g(0).b(0);
    }

    @Override
    public ColorData couleurRaw2() {
        return new ColorData().r(0).g(0).b(0);
    }

    @Override
    public ColorData couleurRaw3() {
        return new ColorData().r(0).g(0).b(0);
    }

    @Override
    public ColorData couleurRaw4() {
        return new ColorData().r(0).g(0).b(0);
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

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

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

    @Override
    public void enablePompe1() {
    }

    @Override
    public void enablePompe2() {
    }

    @Override
    public void enablePompe3() {
    }

    @Override
    public void enablePompe4() {
    }

    @Override
    public void disablePompe1() {
    }

    @Override
    public void disablePompe2() {
    }

    @Override
    public void disablePompe3() {
    }

    @Override
    public void disablePompe4() {
    }
}
