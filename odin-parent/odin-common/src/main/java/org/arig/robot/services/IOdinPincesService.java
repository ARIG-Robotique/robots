package org.arig.robot.services;

import org.arig.robot.model.ECouleur;

public interface IOdinPincesService {
    void activate();

    void deactivate();

    boolean process();

    void processCouleur();

    void releasePompe(boolean gauche, boolean droite);

    void setExpected(ECouleur expected, int pinceNumber);
}
