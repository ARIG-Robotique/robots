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
    protected void releasePompe(final int nb) {
        if (nb == 0) {
            io.releasePompeArriereGauche();
        } else if (nb == 1) {
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
