package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServos;
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
        while(!ioService.alimPuissance5VOk());

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
        ouvrePinceDroite();
        ouvrePinceCentre();
        brasAttentePriseRobot();
    }

    public void ouvrePinceDroite() {
        servos.setPosition(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_OUVERT);
    }

    public void ouvrePinceCentre() {
        if (servos.getPosition(IConstantesServos.PINCE_MODULE_DROIT) != IConstantesServos.PINCE_MODULE_DROIT_CHARGEMENT_VENTOUSE
                && servos.getPosition(IConstantesServos.PINCE_MODULE_DROIT) != IConstantesServos.PINCE_MODULE_DROIT_PRISE_PRODUIT) {
            servos.setPosition(IConstantesServos.PINCE_MODULE_CENTRE, IConstantesServos.PINCE_MODULE_CENTRE_OUVERT);
        }
    }

    public void brasAttentePriseRobot() {
        if (servos.getPosition(IConstantesServos.PINCE_MODULE_DROIT) != IConstantesServos.PINCE_MODULE_DROIT_FERME) {
            servos.setPosition(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_ATTENTE);
            servos.setPosition(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_PRISE_ROBOT);
        }
    }
    
    public void brasAttentePriseFusee() {
        if (servos.getPosition(IConstantesServos.PINCE_MODULE_DROIT) != IConstantesServos.PINCE_MODULE_DROIT_FERME) {
            servos.setPosition(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_ATTENTE);
            servos.setPosition(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_PRISE_FUSEE);
        }
    }

    public void transfertAspiration() {
        servos.setPosition(IConstantesServos.INCLINAISON_ASPIRATION, IConstantesServos.INCLINAISON_ASPI_TRANSFERT);
    }

    public void priseCratereAspiration() {
        servos.setPosition(IConstantesServos.INCLINAISON_ASPIRATION, IConstantesServos.INCLINAISON_ASPI_CRATERE);
    }

    public void fermeAspiration() {
        servos.setPosition(IConstantesServos.INCLINAISON_ASPIRATION, IConstantesServos.INCLINAISON_ASPI_FERME);
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

    public void devidoirChargement() {
        servos.setPosition(IConstantesServos.DEVIDOIR, IConstantesServos.DEVIDOIR_CHARGEMENT);
    }

    public void devidoirLectureCouleur() {
        servos.setPosition(IConstantesServos.DEVIDOIR, IConstantesServos.DEVIDOIR_LECTURE_COULEUR);
    }

    public void devidoirDechargement() {
        servos.setPosition(IConstantesServos.DEVIDOIR, IConstantesServos.DEVIDOIR_DECHARGEMENT);
    }
}
