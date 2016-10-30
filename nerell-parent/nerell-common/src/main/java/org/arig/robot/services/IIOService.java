package org.arig.robot.services;

import org.arig.robot.model.Team;

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
