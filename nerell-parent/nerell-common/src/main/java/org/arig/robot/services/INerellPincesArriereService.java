package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public interface INerellPincesArriereService {

    enum EEcueil {
        JAUNE,
        BLEU,
        EQUIPE
    }

    boolean preparePriseEcueil();

    boolean finalisePriseEcueil(EEcueil ecueil, ECouleurBouee... bouees);

    boolean deposeGrandChenal(ECouleurBouee couleurChenal, boolean partielle);

    void deposeGrandPort();

    boolean deposePetitPort();

    void finalizeDeposeTableEchange();

    void processCouleurBouee();
}
