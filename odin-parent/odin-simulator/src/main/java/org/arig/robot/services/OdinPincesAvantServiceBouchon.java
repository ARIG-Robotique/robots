package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("pincesAvantService")
public class OdinPincesAvantServiceBouchon extends AbstractOdinPincesAvantService {

    @Autowired
    private OdinIOServiceBouchon io;

    @Override
    public void setExpected(ECouleurBouee expected, int pinceNumber) {
        super.setExpected(expected, pinceNumber);

        switch (pinceNumber) {
            case 0:
                io.presenceVentouseAvantGauche(true);
                break;
            case 1:
                io.presenceVentouseAvantDroit(true);
                break;
        }
    }

    @Override
    protected void releasePompes() {
        super.releasePompes();

        io.presenceVentouseAvantGauche(false);
        io.presenceVentouseAvantDroit(false);
    }

    @Override
    public void releasePompe(boolean gauche, boolean droite) {
        super.releasePompe(gauche, droite);

        if (gauche) {
            io.presenceVentouseAvantGauche(false);
        } else {
            io.presenceVentouseAvantDroit(false);
        }
    }
}
