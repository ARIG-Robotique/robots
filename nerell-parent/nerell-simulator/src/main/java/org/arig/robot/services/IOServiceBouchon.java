package org.arig.robot.services;

import lombok.Setter;
import lombok.experimental.Accessors;
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
    private boolean contentPinceAvant1 = false;

    @Setter
    @Accessors(fluent = true)
    private boolean contentPinceAvant2 = false;

    @Setter
    @Accessors(fluent = true)
    private boolean contentPinceAvant3 = false;

    @Setter
    @Accessors(fluent = true)
    private boolean contentPinceAvant4 = false;

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
    public boolean presencePinceAvant1() {
        return contentPinceAvant1;
    }

    @Override
    public boolean presencePinceAvant2() {
        return contentPinceAvant2;
    }

    @Override
    public boolean presencePinceAvant3() {
        return contentPinceAvant3;
    }

    @Override
    public boolean presencePinceAvant4() {
        return contentPinceAvant4;
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

}
