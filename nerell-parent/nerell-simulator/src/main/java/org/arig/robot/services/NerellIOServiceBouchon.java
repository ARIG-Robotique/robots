package org.arig.robot.services;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.ECouleur;
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
    public boolean presence1() {
        return false;
    }

    @Override
    public boolean presence2() {
        return false;
    }

    @Override
    public boolean presence3() {
        return false;
    }

    @Override
    public boolean presence4() {
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
    public ECouleur couleurAvant1() {
        return ECouleur.INCONNU;
    }

    @Override
    public ECouleur couleurAvant2() {
        return ECouleur.INCONNU;
    }

    @Override
    public ECouleur couleurAvant3() {
        return ECouleur.INCONNU;
    }

    @Override
    public ECouleur couleurAvant4() {
        return ECouleur.INCONNU;
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
    public void disableAllPompes() {
    }

    @Override
    public void enableForceAllPompes() {
    }

    @Override
    public void enableForcePompe1() {
    }

    @Override
    public void enableForcePompe2() {
    }

    @Override
    public void enableForcePompe3() {
    }

    @Override
    public void enableForcePompe4() {
    }

    @Override
    public void enableAllPompes() {
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
    public void releaseAllPompes() {
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
