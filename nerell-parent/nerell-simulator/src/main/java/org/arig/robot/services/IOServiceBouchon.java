package org.arig.robot.services;

import lombok.Setter;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IOServiceBouchon implements IIOService {

    @Autowired
    private RobotStatus rs;
    @Setter
    private List<EStrategy> strategies = new ArrayList<>();
    @Setter
    private boolean au = false;
    @Setter
    private boolean tirette = false;
    private boolean alim5V = false;
    private boolean alim12V = false;

    public void setTeam(Team team) {
        rs.setTeam(team);
    }

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
        return false;
    }

    @Override
    public boolean presencePinceAvant2() {
        return false;
    }

    @Override
    public boolean presencePinceAvant3() {
        return false;
    }

    @Override
    public boolean presencePinceAvant4() {
        return false;
    }

    @Override
    public boolean presencePinceArriere1() {
        return false;
    }

    @Override
    public boolean presencePinceArriere2() {
        return false;
    }

    @Override
    public boolean presencePinceArriere3() {
        return false;
    }

    @Override
    public boolean presencePinceArriere4() {
        return false;
    }

    @Override
    public boolean presencePinceArriere5() {
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
