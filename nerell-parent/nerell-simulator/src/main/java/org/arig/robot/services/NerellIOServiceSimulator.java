package org.arig.robot.services;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.CouleurEchantillon;
import org.springframework.stereotype.Service;

@Service("IOService")
public class NerellIOServiceSimulator extends AbstractIOServiceBouchon implements NerellIOService {

    @Setter
    @Accessors(fluent = true)
    private int contentStock = 0;

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique

    @Override
    public boolean presenceVentouseBas() {
        return false;
    }

    @Override
    public boolean presenceVentouseHaut() {
        return false;
    }

    @Override
    public boolean presencePriseBras() {
        return false;
    }

    @Override
    public boolean presenceCarreFouille() {
        return false;
    }

    @Override
    public boolean presenceStock1() {
        return contentStock >= 1;
    }

    @Override
    public boolean presenceStock2() {
        return contentStock >= 2;
    }

    @Override
    public boolean presenceStock3() {
        return contentStock >= 3;
    }

    @Override
    public boolean presenceStock4() {
        return contentStock >= 4;
    }

    @Override
    public boolean presenceStock5() {
        return contentStock >= 5;
    }

    @Override
    public boolean presenceStock6() {
        return contentStock >= 6;
    }

    public CouleurEchantillon couleurVentouseBas() {
        return CouleurEchantillon.ROCHER;
    }

    @Override
    public CouleurEchantillon couleurVentouseHaut() {
        return CouleurEchantillon.ROCHER;
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void enableLedCapteurCouleur() {
        // Nothing to do
    }

    @Override
    public void disableLedCapteurCouleur() {
        // Nothing to do
    }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //


    @Override
    public void disableAllPompes() {
        // Nothing to do
    }

    @Override
    public void enableAllPompes() {
        // Nothing to do
    }

    @Override
    public void enableForceAllPompes() {
        // Nothing to do
    }

    @Override
    public void releaseAllPompes() {
        // Nothing to do
    }

    @Override
    public void enableForcePompeVentouseBas() {
        // Nothing to do
    }

    @Override
    public void enableForcePompeVentouseHaut() {
        // Nothing to do
    }

    @Override
    public void enablePompeVentouseBas() {
        // Nothing to do
    }

    @Override
    public void enablePompeVentouseHaut() {
        // Nothing to do
    }

    @Override
    public void releasePompeVentouseBas() {
        // Nothing to do
    }

    @Override
    public void releasePompeVentouseHaut() {
        // Nothing to do
    }
}
