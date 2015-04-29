package org.arig.eurobot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
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

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
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

    public void end() {
        log.info("Servos en position finale");
        servos.setPosition(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_OUVERT);
        servos.setPosition(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_OUVERT);
        servos.setPositionAndWait(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
        servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
        servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT);
    }

    /* ******************************************************** */
    /* Méthode de contrôle pour les actions de prise de produit */
    /* ******************************************************** */

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

    public void checkAscenseur() {
        if (ioServices.piedCentre() && robotStatus.getNbPied() < IConstantesRobot.nbPiedMax) {
            priseProduitAscenseur();
        }
    }

    public void checkProduitGauche() {
        if (robotStatus.isProduitGauche()) {
            return;
        }

        if (ioServices.produitGauche() || ioServices.gobeletGauche()) {
            priseProduitGauche();
        } else {
            servos.setPosition(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_OUVERT);
            servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS);
        }
    }

    public void checkProduitDroit() {
        if (robotStatus.isProduitDroit()) {
            return;
        }

        if (ioServices.produitDroit() || ioServices.gobeletDroit()) {
            priseProduitDroit();
        } else {
            servos.setPosition(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_OUVERT);
            servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS);
        }
    }

    /* *********************************** */
    /* Méthode unitaire de gestion produit */
    /* *********************************** */

    public void priseProduitAscenseur() {
        log.info("Prise d'un pied au centre");
        servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
        servos.setPositionAndWait(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
        servos.setPositionAndWait(IConstantesServos.PINCE, IConstantesServos.PINCE_FERME);
        robotStatus.incNbPied();
        servos.setPositionAndWait(IConstantesServos.ASCENSEUR, robotStatus.getNbPied() == 4 ? IConstantesServos.ASCENSEUR_PLEIN : IConstantesServos.ASCENSEUR_HAUT);
        log.info("{} pied{} dans l'ascenseur", robotStatus.getNbPied(), robotStatus.getNbPied() > 1 ? "s" : "");
    }

    public void priseProduitGauche() {
        log.info("Produit disponible à gauche");
        servos.setPositionAndWait(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_PRODUIT);
        if (ioServices.gobeletGauche()) {
            servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_HAUT);
        }
        robotStatus.setProduitGauche(true);
        log.info("Produit à gauche [ Pied : {} ; Gobelet {} ]", ioServices.piedGauche(), ioServices.gobeletGauche());
    }

    public void priseProduitDroit() {
        log.info("Produit disponible à droite");
        servos.setPositionAndWait(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_PRODUIT);
        if (ioServices.gobeletDroit()) {
            servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_HAUT);
        }
        robotStatus.setProduitDroit(true);
        log.info("Produit à droite [ Pied : {} ; Gobelet {} ]", ioServices.piedDroit(), ioServices.gobeletDroit());
    }
}
