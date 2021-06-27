package org.arig.robot.services;

public interface IOdinPincesService {
    void deposeGrandPort();

    void deposeGrandChenalRouge();

    void deposeGrandChenalVert();

    void activate();

    boolean processBouee();

    void processCouleurBouee();
}
