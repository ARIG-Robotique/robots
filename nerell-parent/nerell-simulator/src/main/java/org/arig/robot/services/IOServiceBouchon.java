package org.arig.robot.services;

import lombok.Setter;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 30/10/16.
 */
@Service
public class IOServiceBouchon implements IIOService {

    @Autowired
    private RobotStatus rs;

    @Autowired
    @Qualifier("frontColorSensor")
    private TCS34725ColorSensor frontColorSensor;

    @Setter
    private boolean tirette = false;

    @Override
    public boolean btnTapis() {
        return false;
    }

    @Override
    public Team equipe() {
        rs.setTeam(Team.JAUNE);
        return rs.getTeam();
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
    public ColorData frontColor() {
        return frontColorSensor.new ColorData()
                .r((int) (Math.random() * 256))
                .g((int) (Math.random() * 256))
                .b((int) (Math.random() * 256))
                .c((int) (Math.random() * 256));
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
