package org.arig.eurobot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
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
        servos.setPositionAndSpeed(IConstantesServos.PRODUIT_DROIT, IConstantesServos.PRODUIT_DROIT_FERME, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.PRODUIT_GAUCHE, IConstantesServos.PRODUIT_GAUCHE_FERME, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT_PIED, IConstantesServos.SPEED_ASCENSEUR);
        servos.setPositionAndSpeed(IConstantesServos.PINCE, IConstantesServos.PINCE_PRISE_PIED, IConstantesServos.SPEED_PINCE);
        servos.setPositionAndSpeed(IConstantesServos.GUIDE, IConstantesServos.GUIDE_FERME, IConstantesServos.SPEED_GUIDE);
    }

    @Async
    public void deposeProduitDroitFinMatch() {
        log.info("Dépose gobelet droit fin de match");
        if (ioService.produitDroit()) {
            deposeProduitDroit();
        }
    }

    public void deposeProduitDroit() {
        log.info("Dépose gobelet droit");
        servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_MONTE_GB); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.PRODUIT_DROIT, IConstantesServos.PRODUIT_DROIT_OUVERT);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PRODUIT); } catch (InterruptedException e) { }
    }

    @Async
    public void ouvreTapisFinMath() {
        if (robotStatus.isTapisPresent()) {
            log.info("Ouverture pince tapis fin de match");
            servos.setPosition(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_OUVERT);
            servos.setPosition(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_OUVERT);
            if (robotStatus.getTeam() == Team.JAUNE) {
                servos.setPosition(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_CLAP);
            } else {
                servos.setPosition(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_CLAP);
            }
        }
    }

    @Async
    public void deposeProduitGaucheFinMatch() {
        log.info("Dépose gobelet gauche fin de match");
        if (ioService.produitGauche()) {
            deposeProduitGauche();
        }
    }

    public void deposeProduitGauche() {
        log.info("Dépose gobelet gauche");
        servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_MONTE_GB); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.PRODUIT_GAUCHE, IConstantesServos.PRODUIT_GAUCHE_OUVERT);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PRODUIT); } catch (InterruptedException e) { }
    }

    @Async
    public void deposeColonneFinMatch() {
        log.info("Dépose de la colonne en fin de match");
        if (robotStatus.getNbPied() > 0) {
            servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
            try { Thread.currentThread().sleep(IConstantesServos.WAIT_ASCENSEUR); } catch (InterruptedException e) { }
            servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
            servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT);
        }
    }

    public void deposeColonneAuSol() {
        log.info("Dépose de la colonne au sol");
        servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_ASCENSEUR); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PINCE); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_GUIDE); } catch (InterruptedException e) { }
    }

    public void deposeColonneSurTablette() {
        log.info("Dépose de la colonne sur la tablette");
        servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_DEPOSE_BORDURE);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_ASCENSEUR); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PINCE); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_GUIDE); } catch (InterruptedException e) { }
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
        servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_ASCENSEUR); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_PRISE_PIED);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PINCE); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.ASCENSEUR, robotStatus.getNbPied() == IConstantesRobot.nbPiedMax ? IConstantesServos.ASCENSEUR_PLEIN : IConstantesServos.ASCENSEUR_HAUT_PIED);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_ASCENSEUR); } catch (InterruptedException e) { }
    }

    public void priseBalleDansAscenseur() {
        servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_PRISE_BALLE);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PINCE); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT_BALLE);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_ASCENSEUR); } catch (InterruptedException e) { }
    }

    public void ouvrePince() {
        servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
        servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
    }

    @Async
    public void priseProduitGaucheAsync() {
        priseProduitGauche();
    }

    public void priseProduitGauche() {
        log.info("Produit disponible à gauche");
        servos.setPosition(IConstantesServos.PRODUIT_GAUCHE, IConstantesServos.PRODUIT_GAUCHE_FERME);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PRODUIT); } catch (InterruptedException e) { }
        if (ioService.gobeletGauche()) {
            servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_HAUT);
        }
        log.info("Produit à gauche [ Pied : {} ; Gobelet : {} ]", ioService.piedGauche(), ioService.gobeletGauche());

        if (!ioService.produitGauche()) {
            initProduitGauche();
        }
    }

    @Async
    public void ouvrePriseGaucheAsync() {
        ouvrePriseGauche();
    }

    public void ouvrePriseGauche() {
        log.info("Ouverture prise produit gauche");
        servos.setPosition(IConstantesServos.PRODUIT_GAUCHE, IConstantesServos.PRODUIT_GAUCHE_OUVERT);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PRODUIT); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS);
    }

    public void initProduitGauche() {
        servos.setPosition(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_HAUT);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_MONTE_GB); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.PRODUIT_GAUCHE, IConstantesServos.PRODUIT_GAUCHE_INIT);
    }

    @Async
    public void priseProduitDroitAsync() {
        priseProduitDroit();
    }

    public void priseProduitDroit() {
        log.info("Produit disponible à droite");
        servos.setPosition(IConstantesServos.PRODUIT_DROIT, IConstantesServos.PRODUIT_DROIT_FERME);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PRODUIT); } catch (InterruptedException e) { }
        if (ioService.gobeletDroit()) {
            servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_HAUT);
        }
        log.info("Produit à droite [ Pied : {} ; Gobelet : {} ]", ioService.piedDroit(), ioService.gobeletDroit());

        if (!ioService.produitDroit()) {
            initProduitDroit();
        }
    }

    @Async
    public void ouvrePriseDroiteAsync() {
        ouvrePriseDroite();
    }

    public void ouvrePriseDroite() {
        log.info("Ouverture prise produit droit");
        servos.setPosition(IConstantesServos.PRODUIT_DROIT, IConstantesServos.PRODUIT_DROIT_OUVERT);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_PRODUIT); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS);
    }

    public void initProduitDroit() {
        servos.setPosition(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_HAUT);
        try { Thread.currentThread().sleep(IConstantesServos.WAIT_MONTE_GB); } catch (InterruptedException e) { }
        servos.setPosition(IConstantesServos.PRODUIT_DROIT, IConstantesServos.PRODUIT_DROIT_INIT);
    }

    public void fermeGuide() {
        servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_FERME);
    }
}
