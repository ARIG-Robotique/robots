package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

import java.util.List;

public interface IPincesArriereService {

    boolean preparePriseEcueil();

    boolean finalisePriseEcueil(ECouleurBouee... bouees);

    boolean deposeArriereChenal(List<ECouleurBouee> chenal);
}
