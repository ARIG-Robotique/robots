package org.arig.eurobot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Created by gdepuille on 08/03/15.
 */
@Data
public class RobotStatus {

    private boolean asservEnabled = false;

    @Setter(AccessLevel.NONE)
    private int nbPied = 0;

    private boolean gobeletDroit = false;
    private boolean gobeletGauche = false;

    private boolean piedDroit = false;
    private boolean piedGauche = false;

    public void incNbPied() {
        nbPied ++;
    }

    public void resetNbPied() {
        nbPied = 0;
    }
}
