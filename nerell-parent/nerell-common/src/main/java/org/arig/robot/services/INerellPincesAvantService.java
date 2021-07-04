package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public interface INerellPincesAvantService {
    boolean deposeGrandChenal(ECouleurBouee couleurChenal, boolean partielle);

    boolean deposePetitPort();

    void deposeGrandPort();

    void activate();

    void deactivate();

    boolean processBouee();

    void processCouleurBouee();

    void setExpected(ECouleurBouee gauche, ECouleurBouee droite);
}
