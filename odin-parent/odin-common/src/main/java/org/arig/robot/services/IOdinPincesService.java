package org.arig.robot.services;

public interface IOdinPincesService {
    void deposeGrandPort();

    void activate();

    boolean processBouee();

    void processCouleurBouee();
}
