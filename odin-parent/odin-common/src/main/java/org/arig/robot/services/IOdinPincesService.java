package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.GrandChenaux;

public interface IOdinPincesService {
    void deposeGrandPort();

    void deposeFondGrandChenalRouge();

    void deposeFondGrandChenalVert();

    void deposeGrandChenal(ECouleurBouee chenal, GrandChenaux.Line line, int idxGauche, int idxDroite);

    void deposePetitChenal(ECouleurBouee chenal);

    void activate();

    void deactivate();

    boolean processBouee();

    void processCouleurBouee();

    void setExpected(ECouleurBouee expected, int pinceNumber);
}
