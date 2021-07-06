package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("pincesArriereService")
public class NerellPincesArriereServiceBouchon extends AbstractNerellPincesArriereService {

    @Autowired
    private NerellIOServiceBouchon io;

    @Override
    public boolean finalisePriseEcueil(final EEcueil ecueil, final ECouleurBouee... bouees) {
        io.contentPinceArriere(true);

        return super.finalisePriseEcueil(ecueil, bouees);
    }

    @Override
    public boolean deposeGrandChenal(final ECouleurBouee couleurChenal, final boolean partielle) {
        io.contentPinceArriere(false);

        return super.deposeGrandChenal(couleurChenal, partielle);
    }

    @Override
    public boolean deposePetitPort() {
        io.contentPinceArriere(false);

        return super.deposePetitPort();
    }
}
