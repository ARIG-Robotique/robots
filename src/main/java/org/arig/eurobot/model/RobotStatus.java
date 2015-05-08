package org.arig.eurobot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class RobotStatus {

    private Team team;

    @Setter(AccessLevel.NONE)
    private boolean asservEnabled = false;

    public void enableAsserv() { asservEnabled = true; }
    public void disableAsserv() { asservEnabled = false; }

    @Setter(AccessLevel.NONE)
    private boolean avoidanceEnabled = false;

    public void enableAvoidance() { avoidanceEnabled = true; }
    public void disableAvoidance() { avoidanceEnabled = false; }

    @Setter(AccessLevel.NONE)
    private boolean matchEnabled = false;

    public void enableMatch() { matchEnabled = true; }
    public void disableMatch() { matchEnabled = false; }

    @Setter(AccessLevel.NONE)
    private int nbPied = 0;

    public void incNbPied() {
        nbPied ++;
    }
    public void resetNbPied() {
        nbPied = 0;
    }

    private boolean produitDroit = false;
    private boolean produitGauche = false;
}
