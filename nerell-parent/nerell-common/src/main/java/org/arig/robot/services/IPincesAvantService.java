package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.services.AbstractPincesAvantService.Side;

public interface IPincesAvantService {
    boolean deposeGrandChenal(ECouleurBouee couleurChenal);

    boolean deposePetitPort();

    void finaliseDepose();

    void setExpected(Side cote, ECouleurBouee bouee, int pos);

    void activate();

    void disable();

    void process();
}
