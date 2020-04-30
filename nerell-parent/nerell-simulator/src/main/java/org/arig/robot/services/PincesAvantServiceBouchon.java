package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PincesAvantServiceBouchon extends AbstractPincesAvantService {

    @Autowired
    private IOServiceBouchon io;

    @Override
    public void setExpected(final Side cote, final ECouleurBouee bouee, int pos) {
        switch(pos) {
            case 0 : io.contentPinceAvant1(true);break;
            case 1 : io.contentPinceAvant2(true);break;
            case 2 : io.contentPinceAvant3(true);break;
            case 3 : io.contentPinceAvant4(true);break;
        }

        super.setExpected(cote, bouee, pos);
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
