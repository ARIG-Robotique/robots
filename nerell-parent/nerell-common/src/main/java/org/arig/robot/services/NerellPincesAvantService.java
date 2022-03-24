package org.arig.robot.services;

import org.arig.robot.model.Couleur;

public interface NerellPincesAvantService {
    void activate();

    void deactivate();

    boolean process();

    void processCouleur();

    void setExpected(Couleur expected);
}
