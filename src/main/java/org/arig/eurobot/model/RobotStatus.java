package org.arig.eurobot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Created by gdepuille on 08/03/15.
 */
@Data
public class RobotStatus {

    @Setter(AccessLevel.NONE)
    private boolean asservEnabled = false;

    @Setter(AccessLevel.NONE)
    private boolean matchEnabled = false;

    @Setter(AccessLevel.NONE)
    private int nbPied = 0;

    private boolean gobeletDroit = false;
    private boolean gobeletGauche = false;

    private boolean piedDroit = false;
    private boolean piedGauche = false;

    public void enableAsserv() { asservEnabled = true; }

    public void disableAsserv() { asservEnabled = false; }

    public void enableMatch() { matchEnabled = true; }

    public void disableMatch() { matchEnabled = false; }

    public void incNbPied() {
        nbPied ++;
    }

    public void resetNbPied() {
        nbPied = 0;
    }
}
