package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.services.AbstractPincesAvantService.Side;

public interface IPincesAvantService {
    boolean deposeGrandChenal(ECouleurBouee couleurChenal);

    boolean deposePetitPort();

    void finaliseDepose();

    void setExpected(Side cote, ECouleurBouee bouee, int pinceNumber);

    void setEnabled(boolean ena1, boolean ena2, boolean ena3, boolean ena4);

    void activate();

    void disable();

    void process();
}
