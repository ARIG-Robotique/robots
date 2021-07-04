package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public interface IOdinPincesService {
    void deposeGrandPort();

    void deposeGrandChenalRouge();

    void deposeGrandChenalVert();

    void activate();

    void deactivate();

    boolean processBouee();

    void processCouleurBouee();

    void setExpected(ECouleurBouee bouee);
}
