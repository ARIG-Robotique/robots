package org.arig.robot.services;

public interface IOdinPincesAvantService {
    void deposeGrandPort();

    void activate();

    boolean processBouee();

    void processCouleurBouee();
}
