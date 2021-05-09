package org.arig.robot.services;

import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.springframework.stereotype.Service;

@Service("IOService")
public class OdinIOServiceBouchon extends AbstractIOServiceBouchon implements IOdinIOService {

    @Override
    public boolean presenceVentouseAvant1() {
        return false;
    }

    @Override
    public boolean presenceVentouseAvant2() {
        return false;
    }

    @Override
    public boolean presenceVentouseArriere1() {
        return false;
    }

    @Override
    public boolean presenceVentouseArriere2() {
        return false;
    }

    @Override
    public ECouleurBouee couleurBoueeAvant1() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ECouleurBouee couleurBoueeAvant2() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ECouleurBouee couleurBoueeArriere1() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ECouleurBouee couleurBoueeArriere2() {
        return ECouleurBouee.INCONNU;
    }

    @Override
    public ColorData couleurRawAvant1() {
        return new ColorData().r(0).g(0).b(0);
    }

    @Override
    public ColorData couleurRawAvant2() {
        return new ColorData().r(0).g(0).b(0);
    }

    @Override
    public ColorData couleurRawArriere1() {
        return new ColorData().r(0).g(0).b(0);
    }

    @Override
    public ColorData couleurRawArriere2() {
        return new ColorData().r(0).g(0).b(0);
    }

    @Override
    public void enableLedCapteurCouleur() {
    }

    @Override
    public void disableLedCapteurCouleur() {
    }

    @Override
    public void enablePompeAvant1() {
    }

    @Override
    public void enablePompeAvant2() {
    }

    @Override
    public void enablePompeArriere1() {
    }

    @Override
    public void enablePompeArriere2() {
    }

    @Override
    public void disablePompeAvant1() {
    }

    @Override
    public void disablePompeAvant2() {
    }

    @Override
    public void disablePompeArriere1() {
    }

    @Override
    public void disablePompeArriere2() {
    }
}
