package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("pincesAvantService")
public class PincesAvantServiceBouchon extends AbstractPincesAvantService {

    @Autowired
    private IOServiceBouchon io;

    @Override
    public void setExpected(final Side cote, final ECouleurBouee bouee, int pinceNumber) {
        if (cote == Side.LEFT && pinceNumber > 2) {
            throw new IllegalArgumentException("Le coté gauche n'as que les pinces 1 et 2");
        } else if (cote == Side.RIGHT && pinceNumber < 3) {
            throw new IllegalArgumentException("Le coté droit n'as que les pinces 3 et 4");
        }

        switch(pinceNumber) {
            case 1 : io.contentPinceAvant1(true);break;
            case 2 : io.contentPinceAvant2(true);break;
            case 3 : io.contentPinceAvant3(true);break;
            case 4 : io.contentPinceAvant4(true);break;
            default: throw new IllegalArgumentException("Position de pinces inexistantes");
        }

        super.setExpected(cote, bouee, pinceNumber);
    }

    @Override
    public void disable() {
        io.contentPinceAvant1(false);
        io.contentPinceAvant2(false);
        io.contentPinceAvant3(false);
        io.contentPinceAvant4(false);

        super.disable();
    }
}
