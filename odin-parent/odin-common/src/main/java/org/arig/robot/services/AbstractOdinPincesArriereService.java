package org.arig.robot.services;

import org.arig.robot.model.ECouleur;

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
    public void releasePompe(boolean gauche, boolean droite) {
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
    protected ECouleur[] currentState() {
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
    protected void register(int index, ECouleur couleur) {
        rs.pinceArriere(index, couleur);
    }

    @Override
    protected ECouleur getCouleur(int index) {
        // @formatter:off
        switch (index) {
            case 0: return io.couleurArriereGauche();
            case 1: return io.couleurArriereDroit();
            default: return ECouleur.INCONNU;
        }
        // @formatter:on
    }
}
