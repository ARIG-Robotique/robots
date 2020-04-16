package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PincesArriereServiceBouchon extends AbstractPincesArriereService {

    @Autowired
    private IOServiceBouchon io;

    @Override
    public boolean finalisePriseEcueil(ECouleurBouee... bouees) {
        io.contentPinceArriere(true);

        return super.finalisePriseEcueil(bouees);
    }

    @Override
    public boolean deposeArriereChenal(final List<ECouleurBouee> chenal) {
        io.contentPinceArriere(false);

        return super.deposeArriereChenal(chenal);
    }
}
