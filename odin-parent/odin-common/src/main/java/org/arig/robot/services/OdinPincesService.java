package org.arig.robot.services;

import org.arig.robot.model.CouleurEchantillon;

public interface OdinPincesService {
    void activate();

    void deactivate();

    boolean process();

    void processCouleur();

    void releasePompe(boolean gauche, boolean droite);

    void setExpected(CouleurEchantillon expected, int pinceNumber);
}
