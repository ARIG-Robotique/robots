package org.arig.robot.services;

import org.arig.robot.model.ECouleur;

public interface INerellPincesAvantService {
    void activate();

    void deactivate();

    boolean process();

    void processCouleur();

    void setExpected(ECouleur expected);
}
