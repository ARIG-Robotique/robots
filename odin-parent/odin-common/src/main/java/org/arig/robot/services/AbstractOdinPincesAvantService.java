package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;

public abstract class AbstractOdinPincesAvantService extends AbstractOdinPincesService {

    @Override
    protected void disableServicePinces() {
        rs.disablePincesAvant();
    }

    @Override
    protected void releasePompes() {
        io.releasePompesAvant();
    }

    @Override
    protected void releasePompe(boolean gauche, boolean droite) {
        if (gauche) {
            io.releasePompeAvantGauche();
        }
        if (droite) {
            io.releasePompeAvantDroit();
        }
    }

    @Override
    protected void enablePompes() {
        io.enablePompesAvant();
    }

    @Override
    protected ECouleurBouee[] bouees() {
        return rs.pincesAvant();
    }

    @Override
    protected void clearPinces() {
        rs.clearPincesAvant();
    }

    @Override
    protected boolean[] getNewState() {
        return new boolean[]{
                io.presenceVentouseAvantGauche(),
                io.presenceVentouseAvantDroit()
        };
    }

    @Override
    protected void registerBouee(int index, ECouleurBouee couleurBouee) {
        rs.pinceAvant(index, couleurBouee);
    }

    @Override
    protected void pousser(boolean gauche, boolean droite) {
        if (gauche && droite) {
            servos.poussoirAvantGaucheHaut(false);
            servos.poussoirAvantDroitHaut(true);
            servos.poussoirAvantGaucheBas(false);
            servos.poussoirAvantDroitBas(true);
        } else if (gauche) {
            servos.poussoirAvantGaucheHaut(true);
            servos.poussoirAvantGaucheBas(true);
        } else {
            servos.poussoirAvantDroitHaut(true);
            servos.poussoirAvantDroitBas(true);
        }
    }

    @Override
    protected ECouleurBouee getCouleurBouee(int index) {
        // @formatter:off
        switch (index) {
            case 0: return io.couleurBoueeAvantGauche();
            case 1: return io.couleurBoueeAvantDroit();
            default: return ECouleurBouee.INCONNU;
        }
        // @formatter:on
    }
}
