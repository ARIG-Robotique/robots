package org.arig.robot.services;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.springframework.stereotype.Service;

@Service("IOService")
public class NerellIOServiceBouchon extends AbstractIOServiceBouchon implements INerellIOService {

    @Setter
    @Accessors(fluent = true)
    private boolean contentPinceArriere = false;

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique

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
    public void enableLedCapteurCouleur() {
    }

    @Override
    public void disableLedCapteurCouleur() {
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
