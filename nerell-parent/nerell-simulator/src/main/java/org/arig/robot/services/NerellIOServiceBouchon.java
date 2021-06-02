package org.arig.robot.services;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.ECouleurBouee;
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
    public void disableAllPompe() {
    }

    @Override
    public void enableAllPompe() {
    }

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
    public void releaseAllPompe() {
    }

    @Override
    public void releasePompe1() {
    }

    @Override
    public void releasePompe2() {
    }

    @Override
    public void releasePompe3() {
    }

    @Override
    public void releasePompe4() {
    }
}
