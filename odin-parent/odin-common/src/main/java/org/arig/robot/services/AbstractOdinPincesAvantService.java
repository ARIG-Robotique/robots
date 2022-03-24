package org.arig.robot.services;

import org.arig.robot.model.Couleur;

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
    public void releasePompe(boolean gauche, boolean droite) {
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
    protected Couleur[] currentState() {
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
    protected void register(int index, Couleur couleur) {
        rs.pinceAvant(index, couleur);
    }

    @Override
    protected Couleur getCouleur(int index) {
        // @formatter:off
        switch (index) {
            case 0: return io.couleurAvantGauche();
            case 1: return io.couleurAvantDroit();
            default: return Couleur.INCONNU;
        }
        // @formatter:on
    }
}
