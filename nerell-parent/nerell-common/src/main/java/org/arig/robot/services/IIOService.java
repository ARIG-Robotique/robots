package org.arig.robot.services;

import org.arig.robot.model.Team;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;

/**
 * @author gdepuille on 23/04/15.
 */
public interface IIOService {

    boolean btnTapis();
    Team equipe();

    boolean auOk();
    boolean alimServoOk();
    boolean alimMoteurOk();
    boolean tirette();

    boolean buteeAvantGauche();
    boolean buteeAvantDroit();
    boolean buteeArriereGauche();
    boolean buteeArriereDroit();

    boolean produitGauche();
    boolean gobeletGauche();
    boolean piedGauche();

    boolean produitDroit();
    boolean gobeletDroit();
    boolean piedDroit();

    boolean piedCentre();

    ColorData frontColor();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void colorAUKo();
    void clearTeamColor();

    void enableAlimMoteur();
    void disableAlimMoteur();
    void enableAlimServoMoteur();
    void disableAlimServoMoteur();
}
