package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.springframework.stereotype.Service;

@Service("IOService")
public class OdinIOServiceBouchon extends AbstractIOServiceBouchon implements IOdinIOService {

    @Override
    public boolean presenceVentouseAvantGauche() {
        return false;
    }

    @Override
    public boolean presenceVentouseAvantDroit() {
        return false;
    }

    @Override
    public boolean presenceVentouseArriereGauche() {
        return false;
    }

    @Override
    public boolean presenceVentouseArriereDroit() {
        return false;
    }

    @Override
    public ECouleurBouee couleurBoueeAvantGauche() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ECouleurBouee couleurBoueeAvantDroit() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ECouleurBouee couleurBoueeArriereGauche() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ECouleurBouee couleurBoueeArriereDroit() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public void enableLedCapteurCouleur() {
    }

    @Override
    public void disableLedCapteurCouleur() {
    }

    @Override
    public void enablePompeAvantGauche() {
    }

    @Override
    public void enablePompeAvantDroit() {
    }

    @Override
    public void enablePompeArriereGauche() {
    }

    @Override
    public void enablePompeArriereDroit() {
    }

    @Override
    public void releasePompeAvantGauche() {
    }

    @Override
    public void releasePompeAvantDroit() {
    }

    @Override
    public void releasePompeArriereGauche() {
    }

    @Override
    public void releasePompeArriereDroit() {
    }
}
