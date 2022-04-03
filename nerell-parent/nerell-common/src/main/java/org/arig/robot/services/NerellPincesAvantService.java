package org.arig.robot.services;

import org.arig.robot.model.CouleurEchantillon;

public interface NerellPincesAvantService {
    void activate();

    void deactivate();

    boolean process();

    void processCouleur();

    void setExpected(CouleurEchantillon expected);
}
