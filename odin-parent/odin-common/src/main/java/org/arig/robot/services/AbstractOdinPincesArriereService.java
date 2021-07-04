package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public abstract class AbstractOdinPincesArriereService extends AbstractOdinPincesService {

    @Override
    protected void disableServicePinces() {
        rs.disablePincesArriere();
    }

    @Override
    protected void releasePompes() {
        io.releasePompesArriere();
    }

    @Override
    protected void releasePompe(boolean gauche, boolean droite) {
        if (gauche) {
            io.releasePompeArriereGauche();
        }
        if (droite) {
            io.releasePompeArriereDroit();
        }
    }

    @Override
    protected void enablePompes() {
        io.enablePompesArriere();
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rs.pincesArriere();
    }

    @Override
    protected void clearPinces() {
        rs.clearPincesArriere();
    }

    @Override
    protected boolean[] getNewState() {
        return new boolean[]{
                io.presenceVentouseArriereGauche(),
                io.presenceVentouseArriereDroit()
        };
    }

    @Override
    protected void registerBouee(int index, ECouleurBouee couleurBouee) {
        rs.pinceArriere(index, couleurBouee);
    }

    @Override
    protected void pousser(boolean gauche, boolean droite) {
        if (gauche && droite) {
            servos.poussoirArriereGaucheHaut(false);
            servos.poussoirArriereDroitHaut(true);
            servos.poussoirArriereGaucheBas(false);
            servos.poussoirArriereDroitBas(true);
        } else if (gauche) {
            servos.poussoirArriereGaucheHaut(true);
            servos.poussoirArriereGaucheBas(true);
        } else {
            servos.poussoirArriereDroitHaut(true);
            servos.poussoirArriereDroitBas(true);
        }
    }

    @Override
    protected ECouleurBouee getCouleurBouee(int index) {
        // @formatter:off
        switch (index) {
            case 0: return io.couleurBoueeArriereGauche();
            case 1: return io.couleurBoueeArriereDroit();
            default: return ECouleurBouee.INCONNU;
        }
        // @formatter:on
    }
}
