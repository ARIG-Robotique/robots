package org.arig.eurobot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by gdepuille on 27/04/15.
 */
@Slf4j
@Service
public class ServosServices {

    @Autowired
    private SD21Servos servos;

    @Autowired
    private IOServices ioServices;

    @Autowired
    private RobotStatus robotStatus;

    public void setHome() {
        log.info("Servos en position initiale");
        servos.printVersion();
        servos.setPositionAndSpeed(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_HAUT, IConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_HAUT, IConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_FERME, IConstantesServos.SPEED_TAPIS);
        servos.setPositionAndSpeed(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_FERME, IConstantesServos.SPEED_TAPIS);
        servos.setPositionAndSpeed(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_FERME, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_FERME, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS, IConstantesServos.SPEED_MONTE_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS, IConstantesServos.SPEED_MONTE_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT, IConstantesServos.SPEED_ASCENSEUR);
        servos.setPositionAndSpeed(IConstantesServos.PINCE, IConstantesServos.PINCE_FERME, IConstantesServos.SPEED_PINCE);
        servos.setPositionAndSpeed(IConstantesServos.GUIDE, IConstantesServos.GUIDE_FERME, IConstantesServos.SPEED_GUIDE);
        servos.setPositionAndSpeed(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE, IConstantesServos.SPEED_SONAR);
    }


    public void checkBtnTapis() {
        if (ioServices.btnTapis()) {
            log.info("Préparation Nerell demandé");
            servos.setPosition(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_OUVERT);
            servos.setPosition(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_OUVERT);
            servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT);
        } else {
            servos.setPosition(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_FERME);
            servos.setPosition(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_FERME);
            servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_FERME);
        }
    }

    public void checkAscenseur() throws InterruptedException {
        if (ioServices.piedCentre()) {
            log.info("Pied au centre");
            servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
            servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
            Thread.currentThread().sleep(900);
            servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_FERME);
            Thread.currentThread().sleep(300);
            servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT);
            robotStatus.incNbPied();
            Thread.currentThread().sleep(900);
        }
    }

    public void checkProduitGauche() throws InterruptedException {
        if (robotStatus.isProduitGauche()) {
            return;
        }

        if (ioServices.produitGauche() || ioServices.gobeletGauche()) {
            robotStatus.setProduitGauche(true);
            servos.setPosition(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_PRODUIT);
            Thread.currentThread().sleep(1500);
            if (ioServices.gobeletGauche()) {
                servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_HAUT);
            }
            log.info("Produit à gauche [ Pied : {} ; Gobelet {} ]", ioServices.piedGauche(), ioServices.gobeletGauche());
        } else {
            servos.setPosition(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_OUVERT);
            servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS);
        }
    }

    public void checkProduitDroit() throws InterruptedException {
        if (robotStatus.isProduitDroit()) {
            return;
        }

        if (ioServices.produitDroit() || ioServices.gobeletDroit()) {
            robotStatus.setProduitDroit(true);
            servos.setPosition(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_PRODUIT);
            Thread.currentThread().sleep(1500);
            if (ioServices.gobeletDroit()) {
                servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_HAUT);
            }
            log.info("Produit à droite [ Pied : {} ; Gobelet {} ]", ioServices.piedDroit(), ioServices.gobeletDroit());
        } else {
            servos.setPosition(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_OUVERT);
            servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS);
        }
    }
}
