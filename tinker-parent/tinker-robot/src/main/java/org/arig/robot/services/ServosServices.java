package org.arig.robot.services;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServosTinker;

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
        positionFourche = IConstantesServosTinker.POS_FOURCHE_HAUT;
        pca9685GpioProvider.setPwm(IConstantesServosTinker.FOURCHE, positionFourche);
    }

    @Override
    public void fourcheBas() {
        log.info("Fourche bas");
        positionFourche = IConstantesServosTinker.POS_FOURCHE_BAS;
        pca9685GpioProvider.setPwm(IConstantesServosTinker.FOURCHE, positionFourche);
    }

    @Override
    public void toggleFourche() {
        if (positionFourche == IConstantesServosTinker.POS_FOURCHE_BAS) {
            fourcheHaut();
        } else {
            fourcheBas();
        }
    }

    @Override
    public void blocageDroitOuvert() {
        log.info("Blocage droit ouvert");
        positionBlocageDroit = IConstantesServosTinker.POS_BLOCAGE_DROITE_OUVERT;
        pca9685GpioProvider.setPwm(IConstantesServosTinker.BLOCAGE_DROITE, positionBlocageDroit);
    }

    @Override
    public void blocageDroitFerme() {
        log.info("Blocage droit ferme");
        positionBlocageDroit = IConstantesServosTinker.POS_BLOCAGE_DROITE_FERME;
        pca9685GpioProvider.setPwm(IConstantesServosTinker.BLOCAGE_DROITE, positionBlocageDroit);
    }

    @Override
    public void toggleBlocageDroit() {
        if (positionBlocageDroit == IConstantesServosTinker.POS_BLOCAGE_DROITE_OUVERT) {
            blocageDroitFerme();
        } else {
            blocageDroitOuvert();
        }
    }

    @Override
    public void blocageGaucheOuvert() {
        log.info("Blocage Gauche ouvert");
        positionBlocageGauche = IConstantesServosTinker.POS_BLOCAGE_GAUCHE_OUVERT;
        pca9685GpioProvider.setPwm(IConstantesServosTinker.BLOCAGE_GAUCHE, positionBlocageGauche);
    }

    @Override
    public void blocageGaucheFerme() {
        log.info("Blocage Gauche ferme");
        positionBlocageGauche = IConstantesServosTinker.POS_BLOCAGE_GAUCHE_FERME;
        pca9685GpioProvider.setPwm(IConstantesServosTinker.BLOCAGE_GAUCHE, positionBlocageGauche);
    }

    @Override
    public void toggleBlocageGauche() {
        if (positionBlocageGauche == IConstantesServosTinker.POS_BLOCAGE_GAUCHE_OUVERT) {
            blocageGaucheFerme();
        } else {
            blocageGaucheOuvert();
        }
    }

    @Override
    public void translateurGauche() {
        if (positionTranslateur != IConstantesServosTinker.POS_TRANSLATEUR_CENTRE) {
            translateurCentre();
        } else {
            log.info("Translateur gauche");
            positionTranslateur = IConstantesServosTinker.POS_TRANSLATEUR_GAUCHE;
            pca9685GpioProvider.setPwm(IConstantesServosTinker.TRANSLATEUR, positionTranslateur);
        }
    }

    @Override
    public void translateurCentre() {
        log.info("Translateur centre");
        positionTranslateur = IConstantesServosTinker.POS_TRANSLATEUR_CENTRE;
        pca9685GpioProvider.setPwm(IConstantesServosTinker.TRANSLATEUR, positionTranslateur);
    }

    @Override
    public void translateurDroite() {
        if (positionTranslateur != IConstantesServosTinker.POS_TRANSLATEUR_CENTRE) {
            translateurCentre();
        } else {
            log.info("Translateur droite");
            positionTranslateur = IConstantesServosTinker.POS_TRANSLATEUR_DROITE;
            pca9685GpioProvider.setPwm(IConstantesServosTinker.TRANSLATEUR, positionTranslateur);
        }
    }
}
