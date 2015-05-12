package org.arig.eurobot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by gdepuille on 27/04/15.
 */
@Slf4j
@Service
public class ServosService {

    @Autowired
    private SD21Servos servos;

    @Autowired
    private IOService ioService;

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
        servos.setPositionAndSpeed(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_HAUT, IConstantesServos.SPEED_MONTE_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_HAUT, IConstantesServos.SPEED_MONTE_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_PRODUIT, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_PRODUIT, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT_PIED, IConstantesServos.SPEED_ASCENSEUR);
        servos.setPositionAndSpeed(IConstantesServos.PINCE, IConstantesServos.PINCE_PRISE_PIED, IConstantesServos.SPEED_PINCE);
        servos.setPositionAndSpeed(IConstantesServos.GUIDE, IConstantesServos.GUIDE_FERME, IConstantesServos.SPEED_GUIDE);
        servos.setPositionAndSpeed(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE, IConstantesServos.SPEED_SONAR);
    }

    @Async
    public void deposeGobeletDroitFinMatch() {
        log.info("Dépose gobelet droit fin de match");
        deposeGobeletDroit();
    }

    public void deposeGobeletDroit() {
        log.info("Dépose gobelet droit");
        servos.setPositionAndWait(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS);
        servos.setPositionAndWait(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_OUVERT);
    }

    @Async
    public void deposeGobeletGaucheFinMatch() {
        log.info("Dépose gobelet gauche fin de match");
        deposeGobeletGauche();
    }

    public void deposeGobeletGauche() {
        log.info("Dépose gobelet gauche");
        servos.setPositionAndWait(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS);
        servos.setPositionAndWait(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_OUVERT);
    }

    @Async
    public void deposeColonneFinMatch() {
        log.info("Dépose de la colonne en fin de match");
        deposeColonneAuSol();
    }

    public void deposeColonneAuSol() {
        log.info("Dépose de la colonne au sol");
        servos.setPositionAndWait(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
        servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT);
        servos.setPositionAndWait(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
    }

    public void deposeColonneSurTablette() {
        log.info("Dépose de la colonne sur la tablette");
        servos.setPositionAndWait(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_DEPOSE_BORDURE);
        servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT);
        servos.setPositionAndWait(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
    }

    public void leveGobelets() {
        log.info("Leve gobelets");
        servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_HAUT);
        servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_HAUT);
    }

    /* ******************************************************** */
    /* Méthode de contrôle pour les actions de prise de produit */
    /* ******************************************************** */

    public void checkBtnTapis() {
        if (ioService.btnTapis()) {
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
        if (ioService.piedCentre() && robotStatus.getNbPied() < IConstantesRobot.nbPiedMax) {
            priseProduitAscenseur();
        }
    }

    /* *********************************** */
    /* Méthode unitaire de gestion produit */
    /* *********************************** */

    public void priseProduitAscenseur() {
        log.info("Prise d'un pied au centre");
        robotStatus.incNbPied();
        log.info("{} pied{} dans l'ascenseur", robotStatus.getNbPied(), robotStatus.getNbPied() > 1 ? "s" : "");
        servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
        servos.setPositionAndWait(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
        servos.setPositionAndWait(IConstantesServos.PINCE, IConstantesServos.PINCE_PRISE_PIED);
        servos.setPositionAndWait(IConstantesServos.ASCENSEUR, robotStatus.getNbPied() == 4 ? IConstantesServos.ASCENSEUR_PLEIN : IConstantesServos.ASCENSEUR_HAUT_PIED);
    }

    public void priseProduitGauche() {
        log.info("Produit disponible à gauche");
        servos.setPositionAndWait(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_PRODUIT);
        if (ioService.gobeletGauche()) {
            servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_HAUT);
        }
        log.info("Produit à gauche [ Pied : {} ; Gobelet : {} ]", ioService.piedGauche(), ioService.gobeletGauche());
    }

    public void ouvrePriseGauche() {
        log.info("Ouverture prise produit gauche");
        servos.setPosition(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_OUVERT);
        servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS);
    }

    public void fermeProduitGauche() {
        servos.setPosition(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_PRODUIT);
        servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS);
    }

    public void priseProduitDroit() {
        log.info("Produit disponible à droite");
        servos.setPositionAndWait(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_PRODUIT);
        if (ioService.gobeletDroit()) {
            servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_HAUT);
        }
        log.info("Produit à droite [ Pied : {} ; Gobelet : {} ]", ioService.piedDroit(), ioService.gobeletDroit());
    }

    public void ouvrePriseDroite() {
        log.info("Ouverture prise produit droit");
        servos.setPosition(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_OUVERT);
        servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS);
    }

    public void fermeProduitDroit() {
        servos.setPosition(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_PRODUIT);
        servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS);
    }

    public void fermeGuide() {
        servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_FERME);
    }
}
