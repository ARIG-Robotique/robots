package org.arig.robot.tinker.services;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.tinker.constants.TinkerConstantesServos;

@Slf4j
@RequiredArgsConstructor
public class ServosServicesImpl implements ServosServices {

    private final PCA9685GpioProvider pca9685GpioProvider;

    private int positionFourche = -1;
    private int positionBlocageDroit = -1;
    private int positionBlocageGauche = -1;
    private int positionTranslateur = -1;

    @Override
    public void fourcheHaut() {
        log.info("Fourche haut");
        positionFourche = TinkerConstantesServos.POS_FOURCHE_HAUT;
        pca9685GpioProvider.setPwm(TinkerConstantesServos.FOURCHE, positionFourche);
    }

    @Override
    public void fourcheBas() {
        log.info("Fourche bas");
        positionFourche = TinkerConstantesServos.POS_FOURCHE_BAS;
        pca9685GpioProvider.setPwm(TinkerConstantesServos.FOURCHE, positionFourche);
    }

    @Override
    public void toggleFourche() {
        if (positionFourche == TinkerConstantesServos.POS_FOURCHE_BAS) {
            fourcheHaut();
        } else {
            fourcheBas();
        }
    }

    @Override
    public void blocageDroitOuvert() {
        log.info("Blocage droit ouvert");
        positionBlocageDroit = TinkerConstantesServos.POS_BLOCAGE_DROITE_OUVERT;
        pca9685GpioProvider.setPwm(TinkerConstantesServos.BLOCAGE_DROITE, positionBlocageDroit);
    }

    @Override
    public void blocageDroitFerme() {
        log.info("Blocage droit ferme");
        positionBlocageDroit = TinkerConstantesServos.POS_BLOCAGE_DROITE_FERME;
        pca9685GpioProvider.setPwm(TinkerConstantesServos.BLOCAGE_DROITE, positionBlocageDroit);
    }

    @Override
    public void toggleBlocageDroit() {
        if (positionBlocageDroit == TinkerConstantesServos.POS_BLOCAGE_DROITE_OUVERT) {
            blocageDroitFerme();
        } else {
            blocageDroitOuvert();
        }
    }

    @Override
    public void blocageGaucheOuvert() {
        log.info("Blocage Gauche ouvert");
        positionBlocageGauche = TinkerConstantesServos.POS_BLOCAGE_GAUCHE_OUVERT;
        pca9685GpioProvider.setPwm(TinkerConstantesServos.BLOCAGE_GAUCHE, positionBlocageGauche);
    }

    @Override
    public void blocageGaucheFerme() {
        log.info("Blocage Gauche ferme");
        positionBlocageGauche = TinkerConstantesServos.POS_BLOCAGE_GAUCHE_FERME;
        pca9685GpioProvider.setPwm(TinkerConstantesServos.BLOCAGE_GAUCHE, positionBlocageGauche);
    }

    @Override
    public void toggleBlocageGauche() {
        if (positionBlocageGauche == TinkerConstantesServos.POS_BLOCAGE_GAUCHE_OUVERT) {
            blocageGaucheFerme();
        } else {
            blocageGaucheOuvert();
        }
    }

    @Override
    public void translateurGauche() {
        if (positionTranslateur != TinkerConstantesServos.POS_TRANSLATEUR_CENTRE) {
            translateurCentre();
        } else {
            log.info("Translateur gauche");
            positionTranslateur = TinkerConstantesServos.POS_TRANSLATEUR_GAUCHE;
            pca9685GpioProvider.setPwm(TinkerConstantesServos.TRANSLATEUR, positionTranslateur);
        }
    }

    @Override
    public void translateurCentre() {
        log.info("Translateur centre");
        positionTranslateur = TinkerConstantesServos.POS_TRANSLATEUR_CENTRE;
        pca9685GpioProvider.setPwm(TinkerConstantesServos.TRANSLATEUR, positionTranslateur);
    }

    @Override
    public void translateurDroite() {
        if (positionTranslateur != TinkerConstantesServos.POS_TRANSLATEUR_CENTRE) {
            translateurCentre();
        } else {
            log.info("Translateur droite");
            positionTranslateur = TinkerConstantesServos.POS_TRANSLATEUR_DROITE;
            pca9685GpioProvider.setPwm(TinkerConstantesServos.TRANSLATEUR, positionTranslateur);
        }
    }
}
