package org.arig.robot.services;

import org.arig.robot.model.CouleurEchantillon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("pincesArriereService")
public class OdinPincesArriereServiceSimulator extends AbstractOdinPincesArriereService {

    @Autowired
    private OdinIOServiceSimulator io;

    @Override
    public void setExpected(CouleurEchantillon expected, int pinceNumber) {
        super.setExpected(expected, pinceNumber);

        switch (pinceNumber) {
            case 0:
                io.presenceVentouseArriereGauche(true);
                break;
            case 1:
                io.presenceVentouseArriereDroit(true);
                break;
        }
    }

    @Override
    protected void releasePompes() {
        super.releasePompes();

        io.presenceVentouseArriereGauche(false);
        io.presenceVentouseArriereDroit(false);
    }

    @Override
    public void releasePompe(boolean gauche, boolean droite) {
        super.releasePompe(gauche, droite);

        if (gauche) {
            io.presenceVentouseArriereGauche(false);
        } else {
            io.presenceVentouseArriereDroit(false);
        }
    }
}
