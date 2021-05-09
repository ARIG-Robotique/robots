package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public interface INerellPincesArriereService {

    boolean preparePriseEcueil();

    boolean finalisePriseEcueil(ECouleurBouee... bouees);

    boolean deposeGrandChenal(ECouleurBouee couleurChenal, boolean partielle);

    void deposeGrandPort();

    boolean deposePetitPort();
}
