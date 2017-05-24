package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.exception.ServoException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 27/04/15.
 */
@Slf4j
@Service
public class ServosService {

    @Autowired
    private SD21Servos servos;

    @Autowired
    private IIOService ioService;

    @Autowired
    private RobotStatus robotStatus;

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void cyclePreparation() {
        log.info("Servos en position initiale");
        servos.printVersion();

        // Moteurs
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_DROIT, 1500, (byte) 0);
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_GAUCHE, 1500, (byte) 0);
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_EJECTION, 1500, (byte) 0);
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_ROULEAUX, 1500, (byte) 0);
        servos.setSpeed(IConstantesServos.MOTOR_ASPIRATION, (byte) 0);
        aspirationStop();

        ioService.enableAlim5VPuissance();
        while (!ioService.alimPuissance5VOk()) ;

        // Tous en même temps
        servos.setPositionAndSpeed(IConstantesServos.PORTE_MAGASIN_DROIT, IConstantesServos.PORTE_DROITE_OUVERT, IConstantesServos.SPEED_PORTE_MAG);
        servos.setPositionAndSpeed(IConstantesServos.PORTE_MAGASIN_GAUCHE, IConstantesServos.PORTE_GAUCHE_OUVERT, IConstantesServos.SPEED_PORTE_MAG);
        servos.setPositionAndSpeed(IConstantesServos.BLOCAGE_ENTREE_MAG, IConstantesServos.BLOCAGE_OUVERT, IConstantesServos.SPEED_BLOCAGE_MAG);
        servos.setPositionAndSpeed(IConstantesServos.DEVIDOIR, IConstantesServos.DEVIDOIR_CHARGEMENT, IConstantesServos.SPEED_DEVIDOIR);
        servos.setPositionAndSpeed(IConstantesServos.INCLINAISON_ASPIRATION, IConstantesServos.INCLINAISON_ASPI_TRANSFERT, IConstantesServos.SPEED_INC_ASPI);

        // Ordre précis car blocage mécanique dans certains cas
        servos.setPositionAndSpeed(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_DEPOSE_MAGASIN, IConstantesServos.SPEED_ROT_VENTOUSE);
        servos.waitTime(IConstantesServos.WAIT_ROT_VENTOUSE);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_MODULE_CENTRE, IConstantesServos.PINCE_MODULE_CENTRE_FERME, IConstantesServos.SPEED_PINCE);
        servos.waitTime(IConstantesServos.WAIT_PINCE);
        servos.setPositionAndSpeed(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_DEPOSE, IConstantesServos.SPEED_INC_BRAS);
        servos.waitTime(IConstantesServos.WAIT_INC_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_FERME, IConstantesServos.SPEED_PINCE);
    }

    public void homes() {
        pinceDroiteOuvert();
        pinceCentreOuvert();
        brasAttentePriseRobot();
    }

    public void waitPince() {
        servos.waitTime(IConstantesServos.WAIT_PINCE);
    }

    public void waitBras() {
        servos.waitTime(IConstantesServos.WAIT_INC_BRAS);
    }

    public void waitPorteMagasin() {
        servos.waitTime(IConstantesServos.WAIT_PORTE_MAG);
    }

    public void waitDevidoire() {
        servos.waitTime(IConstantesServos.WAIT_DEVIDOIR);
    }

    public void waitBlocageMagasin() {
        servos.waitTime(IConstantesServos.WAIT_BLOCAGE_MAG);
    }

    public void waitAspiration() {
        servos.waitTime(IConstantesServos.WAIT_INC_ASPI);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//
    public boolean isPinceDroiteOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_MODULE_DROIT) == IConstantesServos.PINCE_MODULE_DROIT_OUVERT;
    }

    public boolean isPinceDroitePriseProduit() {
        return servos.getPosition(IConstantesServos.PINCE_MODULE_DROIT) == IConstantesServos.PINCE_MODULE_DROIT_PRISE_PRODUIT;
    }

    public boolean isPinceDroiteVentouse() {
        return servos.getPosition(IConstantesServos.PINCE_MODULE_DROIT) == IConstantesServos.PINCE_MODULE_DROIT_CHARGEMENT_VENTOUSE;
    }

    public boolean isPinceDroiteFerme() {
        return servos.getPosition(IConstantesServos.PINCE_MODULE_DROIT) == IConstantesServos.PINCE_MODULE_DROIT_FERME;
    }

    public boolean isPinceCentreOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_MODULE_CENTRE) == IConstantesServos.PINCE_MODULE_CENTRE_OUVERT;
    }

    public boolean isPinceCentreOuvertDansDroit() {
        return servos.getPosition(IConstantesServos.PINCE_MODULE_CENTRE) == IConstantesServos.PINCE_MODULE_CENTRE_OUVERT_DANS_DROIT;
    }

    public boolean isPinceCentreFerme() {
        return servos.getPosition(IConstantesServos.PINCE_MODULE_CENTRE) == IConstantesServos.PINCE_MODULE_CENTRE_FERME;
    }

    public boolean isBrasPriseRobot() {
        return servos.getPosition(IConstantesServos.INCLINAISON_BRAS) == IConstantesServos.INCLINAISON_BRAS_PRISE_ROBOT;
    }

    public boolean isBrasPriseFusee() {
        return servos.getPosition(IConstantesServos.INCLINAISON_BRAS) == IConstantesServos.INCLINAISON_BRAS_PRISE_FUSEE;
    }

    public boolean isBrasAttente() {
        return servos.getPosition(IConstantesServos.INCLINAISON_BRAS) == IConstantesServos.INCLINAISON_BRAS_ATTENTE;
    }

    public boolean isBrasDepose() {
        return servos.getPosition(IConstantesServos.INCLINAISON_BRAS) == IConstantesServos.INCLINAISON_BRAS_DEPOSE;
    }

    public boolean isBrasVertical() {
        return servos.getPosition(IConstantesServos.INCLINAISON_BRAS) == IConstantesServos.INCLINAISON_BRAS_VERTICAL;
    }

    public boolean isVentousePriseRobot() {
        return servos.getPosition(IConstantesServos.ROTATION_VENTOUSE) == IConstantesServos.ROTATION_VENTOUSE_PRISE_ROBOT;
    }

    public boolean isVentousePriseFusee() {
        return servos.getPosition(IConstantesServos.ROTATION_VENTOUSE) == IConstantesServos.ROTATION_VENTOUSE_PRISE_FUSEE;
    }

    public boolean isVentouseDepose() {
        return servos.getPosition(IConstantesServos.ROTATION_VENTOUSE) == IConstantesServos.ROTATION_VENTOUSE_DEPOSE_MAGASIN;
    }

    public boolean isPorteFerme() {
        return servos.getPosition(IConstantesServos.PORTE_MAGASIN_DROIT) == IConstantesServos.PORTE_DROITE_FERME ||
                servos.getPosition(IConstantesServos.PORTE_MAGASIN_GAUCHE) == IConstantesServos.PORTE_GAUCHE_FERME;
    }

    public boolean isAspiCallage() {
        return servos.getPosition(IConstantesServos.INCLINAISON_ASPIRATION) == IConstantesServos.INCLINAISON_ASPI_INIT_CALAGE;
    }

    public boolean isAspiTransfert() {
        return servos.getPosition(IConstantesServos.INCLINAISON_ASPIRATION) == IConstantesServos.INCLINAISON_ASPI_TRANSFERT;
    }

    public boolean isAspiCratere() {
        return servos.getPosition(IConstantesServos.INCLINAISON_ASPIRATION) == IConstantesServos.INCLINAISON_ASPI_CRATERE;
    }

    public boolean isAspiFerme() {
        return servos.getPosition(IConstantesServos.INCLINAISON_ASPIRATION) == IConstantesServos.INCLINAISON_ASPI_FERME;
    }

    public boolean isDevidoirChargement() {
        return servos.getPosition(IConstantesServos.DEVIDOIR) == IConstantesServos.DEVIDOIR_CHARGEMENT;
    }

    public boolean isDevidoirDechargement() {
        return servos.getPosition(IConstantesServos.DEVIDOIR) == IConstantesServos.DEVIDOIR_DECHARGEMENT;
    }

    public boolean isDevidoirLectureCouleur() {
        return servos.getPosition(IConstantesServos.DEVIDOIR) == IConstantesServos.DEVIDOIR_LECTURE_COULEUR;
    }

    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void pinceDroiteOuvert() {
        servos.setPosition(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_OUVERT);
    }

    public void pinceDroitePriseProduit() {
        servos.setPosition(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_PRISE_PRODUIT);
    }

    public void pinceDroiteVentouse() {
        if (isPinceCentreFerme() && (isPinceDroiteOuvert() || isPinceDroitePriseProduit())) {
            servos.setPosition(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_CHARGEMENT_VENTOUSE);
        } else {
            throw new ServoException("pinceDroite");
        }
    }

    public void pinceDroiteFerme() {
        if (isPinceCentreFerme() && !isBrasPriseRobot() && !isBrasAttente()) {
            servos.setPosition(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_FERME);
        } else {
            throw new ServoException("pinceDroite");
        }
    }

    public void pinceCentreOuvertDansDroit() {
        if ((isPinceDroiteOuvert() || isPinceDroitePriseProduit()) && (isPinceCentreOuvert() || (!isBrasPriseRobot() && !isBrasAttente()))) {
            servos.setPosition(IConstantesServos.PINCE_MODULE_CENTRE, IConstantesServos.PINCE_MODULE_CENTRE_OUVERT_DANS_DROIT);
        } else {
            throw new ServoException("pinceCentre");
        }
    }

    public void pinceCentreOuvert() {
        if ((isPinceDroiteOuvert() || isPinceDroitePriseProduit()) && (isPinceCentreOuvertDansDroit() || (!isBrasAttente() && !isBrasPriseRobot()))) {
            servos.setPosition(IConstantesServos.PINCE_MODULE_CENTRE, IConstantesServos.PINCE_MODULE_CENTRE_OUVERT);
        } else {
            throw new ServoException("pinceCentre");
        }
    }

    public void pinceCentreFerme() {
        if (isBrasVertical() && isVentouseDepose() || isBrasPriseFusee()) {
            servos.setPosition(IConstantesServos.PINCE_MODULE_CENTRE, IConstantesServos.PINCE_MODULE_CENTRE_FERME);
        } else {
            throw new ServoException("pinceCentre");
        }
    }

    public void brasAttentePriseRobot() {
        if (checkDescenteBras()) {
            servos.setPosition(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_ATTENTE);
            waitBras();
            servos.setPosition(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_PRISE_ROBOT);
        } else {
            throw new ServoException("bras");
        }
    }

    public void brasAttentePriseFusee() {
        if (checkDescenteBras()) {
            servos.setPosition(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_ATTENTE);
            waitBras();
            servos.setPosition(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_PRISE_FUSEE);
        } else {
            throw new ServoException("bras");
        }
    }

    public void brasAttenteDepose() {
        if (checkDescenteBras()) {
            servos.setPosition(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_DEPOSE_MAGASIN);
            servos.setPosition(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_ATTENTE);
        } else {
            throw new ServoException("bras");
        }
    }

    public void brasDepose() {
        if (!isPinceDroiteFerme() && (
                isBrasDepose() ||
                        isPinceCentreFerme() ||
                        isPinceCentreOuvertDansDroit() ||
                        isPinceCentreOuvert() && !ioService.presenceModuleDansBras()
        )) {
            servos.setPosition(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_DEPOSE_MAGASIN);
            servos.setPosition(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_DEPOSE);
        } else {
            throw new ServoException("bras");
        }
    }

    public void brasPriseFusee() {
        if (checkDescenteBras()) {
            servos.setPosition(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_PRISE_FUSEE);
            servos.setPosition(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_PRISE_FUSEE);
        } else {
            throw new ServoException("bras");
        }
    }

    public void brasPriseRobot() {
        if (checkDescenteBras()) {
            servos.setPosition(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_PRISE_ROBOT);
            servos.waitTime(IConstantesServos.WAIT_ROT_VENTOUSE);
            servos.setPosition(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_PRISE_ROBOT);
        } else {
            throw new ServoException("bras");
        }
    }

    public void brasVertical() {
        if (checkDescenteBras()) {
            servos.setPosition(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_VERTICAL);
            servos.setPosition(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_DEPOSE_MAGASIN);
        } else {
            throw new ServoException("bras");
        }
    }

    private boolean checkDescenteBras() {
        return !(isBrasDepose() && isPorteFerme() && ioService.presenceModuleDansBras())
                && !isPinceDroiteFerme()
                && (
                isBrasAttente() ||
                        isBrasVertical() ||
                        isBrasPriseRobot() ||
                        isBrasPriseFusee() ||
                        isPinceCentreFerme() ||
                        isPinceCentreOuvertDansDroit() ||
                        isPinceCentreOuvert() && !ioService.presenceModuleDansBras()
        );
    }

    public void aspirationInitCallage() {
        servos.setPosition(IConstantesServos.INCLINAISON_ASPIRATION, IConstantesServos.INCLINAISON_ASPI_INIT_CALAGE);
    }

    public void aspirationTransfert() {
        servos.setPosition(IConstantesServos.INCLINAISON_ASPIRATION, IConstantesServos.INCLINAISON_ASPI_TRANSFERT);
    }

    public void aspirationCratere() {
        servos.setPosition(IConstantesServos.INCLINAISON_ASPIRATION, IConstantesServos.INCLINAISON_ASPI_CRATERE);
    }

    public void aspirationFerme() {
        servos.setPosition(IConstantesServos.INCLINAISON_ASPIRATION, IConstantesServos.INCLINAISON_ASPI_FERME);
    }

    public void porteMagasinFerme() {
        servos.setPosition(IConstantesServos.PORTE_MAGASIN_GAUCHE, IConstantesServos.PORTE_GAUCHE_FERME);
        servos.setPosition(IConstantesServos.PORTE_MAGASIN_DROIT, IConstantesServos.PORTE_DROITE_FERME);
    }

    public void porteMagasinOuvert() {
        servos.setPosition(IConstantesServos.PORTE_MAGASIN_GAUCHE, IConstantesServos.PORTE_GAUCHE_OUVERT);
        servos.setPosition(IConstantesServos.PORTE_MAGASIN_DROIT, IConstantesServos.PORTE_DROITE_OUVERT);
    }

    public void entreeMagasinOuvert() {
        servos.setPosition(IConstantesServos.BLOCAGE_ENTREE_MAG, IConstantesServos.BLOCAGE_OUVERT);
    }

    public void entreeMagasinFerme() {
        servos.setPosition(IConstantesServos.BLOCAGE_ENTREE_MAG, IConstantesServos.BLOCAGE_FERME);
    }

    public void devidoirChargement() {
        servos.setPosition(IConstantesServos.DEVIDOIR, IConstantesServos.DEVIDOIR_CHARGEMENT);
    }

    public void devidoirDechargement() {
        servos.setPosition(IConstantesServos.DEVIDOIR, IConstantesServos.DEVIDOIR_DECHARGEMENT);
    }

    public void devidoirLectureCouleur() {
        servos.setPosition(IConstantesServos.DEVIDOIR, IConstantesServos.DEVIDOIR_LECTURE_COULEUR);
    }

    public void aspirationMax() {
        servos.setPosition(IConstantesServos.MOTOR_ASPIRATION, IConstantesServos.MOTOR_ASPIRATION_FULL);
    }

    public void aspirationStop() {
        servos.setPosition(IConstantesServos.MOTOR_ASPIRATION, IConstantesServos.MOTOR_ASPIRATION_STOP);
    }

    public void stopGlissiere() {
        servos.setPosition(IConstantesServos.MOTOR_EJECTION, IConstantesServos.MOTOR_STOP);
    }

    public void ouvreGlissiere() {
        // Ouverture si pas complètement sorti
        if (ioService.finCourseGlissiereGauche()) {
            servos.setPosition(IConstantesServos.MOTOR_EJECTION, IConstantesServos.MOTOR_FORWARD_FULL);
        }
    }

    public void fermeGlissiere() {
        // Fermeture si pas complètement fermé
        if (ioService.finCourseGlissiereDroite()) {
            servos.setPosition(IConstantesServos.MOTOR_EJECTION, IConstantesServos.MOTOR_REVERSE_FULL);
        }
    }

    public void tourneModuleRouleauxFF() {
        servos.setPosition(IConstantesServos.MOTOR_ROULEAUX, IConstantesServos.MOTOR_FORWARD_FULL);
    }

    public void tourneModuleRouleauxRF() {
        servos.setPosition(IConstantesServos.MOTOR_ROULEAUX, IConstantesServos.MOTOR_REVERSE_FULL);
    }

    public void tourneModuleRouleauxFM() {
        servos.setPosition(IConstantesServos.MOTOR_ROULEAUX, IConstantesServos.MOTOR_FORWARD_MEDIUM);
    }

    public void tourneModuleRouleauxRM() {
        servos.setPosition(IConstantesServos.MOTOR_ROULEAUX, IConstantesServos.MOTOR_REVERSE_MEDIUM);
    }

    public void tourneModuleRouleauxStop() {
        servos.setPosition(IConstantesServos.MOTOR_ROULEAUX, IConstantesServos.MOTOR_STOP);
    }
}
