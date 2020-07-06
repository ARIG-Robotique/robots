package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServosServices implements IServosServices {

    @Override
    public void fourcheHaut() {
        log.info("Fourche haut");
    }

    @Override
    public void fourcheBas() {
        log.info("Fourche bas");
    }

    @Override
    public void blocageDroitOuvert() {
        log.info("Blocage droit ouvert");
    }

    @Override
    public void blocageDroitFerme() {
        log.info("Blocage droit ferme");
    }

    @Override
    public void blocageGaucheOuvert() {
        log.info("Blocage gauche ouvert");
    }

    @Override
    public void blocageGaucheFerme() {
        log.info("Blocage gauche ferme");
    }

    @Override
    public void translateurDroite() {
        log.info("Translateur droite");
    }

    @Override
    public void transleteurGauche() {
        log.info("Translateur gauche");
    }

    @Override
    public void translateurCentre() {
        log.info("Translateur centre");
    }
}
