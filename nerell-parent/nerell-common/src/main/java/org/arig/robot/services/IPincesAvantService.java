package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public interface IPincesAvantService {
    boolean deposeGrandChenal(ECouleurBouee couleurChenal, boolean partielle);

    boolean deposePetitPort();

    void deposeGrandPort();

    void finaliseDepose();

    void activate();

    void process();
}
