package org.arig.robot.tinker.services;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.tinker.constants.ITinkerConstantesServos;

@Slf4j
@RequiredArgsConstructor
public class ServosServices implements IServosServices {

    private final PCA9685GpioProvider pca9685GpioProvider;

    private int positionFourche = -1;
    private int positionBlocageDroit = -1;
    private int positionBlocageGauche = -1;
    private int positionTranslateur = -1;

    @Override
    public void fourcheHaut() {
        log.info("Fourche haut");
        positionFourche = ITinkerConstantesServos.POS_FOURCHE_HAUT;
        pca9685GpioProvider.setPwm(ITinkerConstantesServos.FOURCHE, positionFourche);
    }

    @Override
    public void fourcheBas() {
        log.info("Fourche bas");
        positionFourche = ITinkerConstantesServos.POS_FOURCHE_BAS;
        pca9685GpioProvider.setPwm(ITinkerConstantesServos.FOURCHE, positionFourche);
    }

    @Override
    public void toggleFourche() {
        if (positionFourche == ITinkerConstantesServos.POS_FOURCHE_BAS) {
            fourcheHaut();
        } else {
            fourcheBas();
        }
    }

    @Override
    public void blocageDroitOuvert() {
        log.info("Blocage droit ouvert");
        positionBlocageDroit = ITinkerConstantesServos.POS_BLOCAGE_DROITE_OUVERT;
        pca9685GpioProvider.setPwm(ITinkerConstantesServos.BLOCAGE_DROITE, positionBlocageDroit);
    }

    @Override
    public void blocageDroitFerme() {
        log.info("Blocage droit ferme");
        positionBlocageDroit = ITinkerConstantesServos.POS_BLOCAGE_DROITE_FERME;
        pca9685GpioProvider.setPwm(ITinkerConstantesServos.BLOCAGE_DROITE, positionBlocageDroit);
    }

    @Override
    public void toggleBlocageDroit() {
        if (positionBlocageDroit == ITinkerConstantesServos.POS_BLOCAGE_DROITE_OUVERT) {
            blocageDroitFerme();
        } else {
            blocageDroitOuvert();
        }
    }

    @Override
    public void blocageGaucheOuvert() {
        log.info("Blocage Gauche ouvert");
        positionBlocageGauche = ITinkerConstantesServos.POS_BLOCAGE_GAUCHE_OUVERT;
        pca9685GpioProvider.setPwm(ITinkerConstantesServos.BLOCAGE_GAUCHE, positionBlocageGauche);
    }

    @Override
    public void blocageGaucheFerme() {
        log.info("Blocage Gauche ferme");
        positionBlocageGauche = ITinkerConstantesServos.POS_BLOCAGE_GAUCHE_FERME;
        pca9685GpioProvider.setPwm(ITinkerConstantesServos.BLOCAGE_GAUCHE, positionBlocageGauche);
    }

    @Override
    public void toggleBlocageGauche() {
        if (positionBlocageGauche == ITinkerConstantesServos.POS_BLOCAGE_GAUCHE_OUVERT) {
            blocageGaucheFerme();
        } else {
            blocageGaucheOuvert();
        }
    }

    @Override
    public void translateurGauche() {
        if (positionTranslateur != ITinkerConstantesServos.POS_TRANSLATEUR_CENTRE) {
            translateurCentre();
        } else {
            log.info("Translateur gauche");
            positionTranslateur = ITinkerConstantesServos.POS_TRANSLATEUR_GAUCHE;
            pca9685GpioProvider.setPwm(ITinkerConstantesServos.TRANSLATEUR, positionTranslateur);
        }
    }

    @Override
    public void translateurCentre() {
        log.info("Translateur centre");
        positionTranslateur = ITinkerConstantesServos.POS_TRANSLATEUR_CENTRE;
        pca9685GpioProvider.setPwm(ITinkerConstantesServos.TRANSLATEUR, positionTranslateur);
    }

    @Override
    public void translateurDroite() {
        if (positionTranslateur != ITinkerConstantesServos.POS_TRANSLATEUR_CENTRE) {
            translateurCentre();
        } else {
            log.info("Translateur droite");
            positionTranslateur = ITinkerConstantesServos.POS_TRANSLATEUR_DROITE;
            pca9685GpioProvider.setPwm(ITinkerConstantesServos.TRANSLATEUR, positionTranslateur);
        }
    }
}
