package org.arig.robot.services;

import lombok.Setter;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 30/10/16.
 */
@Service
public class IOServiceBouchon implements IIOService {

    @Setter
    private boolean tirette = false;

    @Override
    public boolean btnTapis() {
        return false;
    }

    @Override
    public Team equipe() {
        return Team.JAUNE;
    }

    @Override
    public boolean auOk() {
        return true;
    }

    @Override
    public boolean alimServoOk() {
        return true;
    }

    @Override
    public boolean alimMoteurOk() {
        return true;
    }

    @Override
    public boolean tirette() {
        return tirette;
    }

    @Override
    public boolean buteeAvantGauche() {
        return false;
    }

    @Override
    public boolean buteeAvantDroit() {
        return false;
    }

    @Override
    public boolean buteeArriereGauche() {
        return false;
    }

    @Override
    public boolean buteeArriereDroit() {
        return false;
    }

    @Override
    public boolean produitGauche() {
        return false;
    }

    @Override
    public boolean gobeletGauche() {
        return false;
    }

    @Override
    public boolean piedGauche() {
        return false;
    }

    @Override
    public boolean produitDroit() {
        return false;
    }

    @Override
    public boolean gobeletDroit() {
        return false;
    }

    @Override
    public boolean piedDroit() {
        return false;
    }

    @Override
    public boolean piedCentre() {
        return false;
    }

    @Override
    public void colorAUKo() {

    }

    @Override
    public void clearTeamColor() {

    }

    @Override
    public void enableAlimMoteur() {

    }

    @Override
    public void disableAlimMoteur() {

    }

    @Override
    public void enableAlimServoMoteur() {

    }

    @Override
    public void disableAlimServoMoteur() {

    }
}
