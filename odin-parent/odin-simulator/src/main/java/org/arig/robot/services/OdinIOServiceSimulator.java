package org.arig.robot.services;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.Couleur;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Accessors(fluent = true)
@Service("IOService")
public class OdinIOServiceSimulator extends AbstractIOServiceBouchon implements OdinIOService {

    private boolean presenceVentouseAvantGauche = false;
    private boolean presenceVentouseAvantDroit = false;
    private boolean presenceVentouseArriereGauche = false;
    private boolean presenceVentouseArriereDroit = false;

    @Override
    public boolean presenceAvantGauche() {
        return false;
    }

    @Override
    public boolean presenceAvantDroit() {
        return false;
    }

    @Override
    public boolean presenceArriereGauche() {
        return false;
    }

    @Override
    public boolean presenceArriereDroit() {
        return false;
    }

    @Override
    public Couleur couleurAvantGauche() {
        return Couleur.INCONNU;
    }

    @Override
    public Couleur couleurAvantDroit() {
        return Couleur.INCONNU;
    }

    @Override
    public Couleur couleurArriereGauche() {
        return Couleur.INCONNU;
    }

    @Override
    public Couleur couleurArriereDroit() {
        return Couleur.INCONNU;
    }

    @Override
    public void enableLedCapteurCouleur() {
    }

    @Override
    public void disableLedCapteurCouleur() {
    }

    @Override
    public void disableAllPompe() {
    }

    @Override
    public void enableAllPompe() {
    }

    @Override
    public void enablePompesAvant() {
    }

    @Override
    public void enablePompesArriere() {
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
    public void releaseAllPompe() {
    }

    @Override
    public void releasePompesAvant() {
    }

    @Override
    public void releasePompesArriere() {
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
