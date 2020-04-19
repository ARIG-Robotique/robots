package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public interface IPincesArriereService {

    boolean preparePriseEcueil();

    boolean finalisePriseEcueil(ECouleurBouee... bouees);

    boolean deposeArriereChenal(ECouleurBouee chenal);
}
