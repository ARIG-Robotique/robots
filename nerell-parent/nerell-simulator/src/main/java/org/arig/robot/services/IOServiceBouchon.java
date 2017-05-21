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
    public Team equipe() {
        rs.setTeam(Team.JAUNE);
        return rs.getTeam();
    }

    @Override
    public boolean auOk() {
        return true;
    }

    @Override
    public boolean alimPuissance5VOk() {
        return true;
    }

    @Override
    public boolean alimPuissance8VOk() {
        return true;
    }

    @Override
    public boolean alimPuissance12VOk() {
        return true;
    }

    @Override
    public boolean tirette() {
        return tirette;
    }

    @Override
    public boolean ledCapteurCouleur() {
        return true;
    }

    @Override
    public boolean bordureAvant() {
        return false;
    }

    @Override
    public boolean bordureArriereDroite() {
        return false;
    }

    @Override
    public boolean bordureArriereGauche() {
        return false;
    }

    @Override
    public boolean presenceEntreeMagasin() {
        return false;
    }

    @Override
    public boolean presenceDevidoir() {
        return false;
    }

    @Override
    public boolean presencePinceDroite() {
        return false;
    }

    @Override
    public boolean presencePinceCentre() {
        return false;
    }

    @Override
    public boolean presenceBaseLunaireDroite() {
        return false;
    }

    @Override
    public boolean presenceBaseLunaireGauche() {
        return false;
    }

    @Override
    public boolean presenceBallesAspiration() {
        return false;
    }

    @Override
    public boolean presenceRouleaux() {
        return false;
    }

    @Override
    public boolean presenceFusee() {
        return false;
    }

    @Override
    public boolean finCourseGlissiereDroite() {
        return false;
    }

    @Override
    public boolean finCourseGlissiereGauche() {
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
    public void colorLedRGBKo() {

    }

    @Override
    public void colorLedRGBOk() {

    }

    @Override
    public void clearColorLedRGB() {

    }

    @Override
    public void teamColorLedRGB() {

    }

    @Override
    public void enableLedCapteurCouleur() {

    }

    @Override
    public void disableLedCapteurCouleur() {

    }

    @Override
    public void enableAlim5VPuissance() {

    }

    @Override
    public void disableAlim5VPuissance() {

    }

    @Override
    public void enableAlim8VPuissance() {

    }

    @Override
    public void disableAlim8VPuissance() {

    }

    @Override
    public void enableAlim12VPuissance() {

    }

    @Override
    public void disableAlim12VPuissance() {

    }

    @Override
    public void enableElectroVanne() {

    }

    @Override
    public void disableElectroVanne() {

    }

    @Override
    public void enablePompeAVide() {

    }

    @Override
    public void disablePompeAVide() {

    }
}
