package org.arig.robot.web.controller;

import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.model.servos.ServoConfig;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * @author gdepuille on 21/12/13.
 */
@RestController
public class ServosController extends AbstractServosController {

    private static List<ServoConfig> servoConfigs = new LinkedList<>();

    static {
        ServoConfig pinceModuleDroit = new ServoConfig();
        pinceModuleDroit.setId(IConstantesServos.PINCE_MODULE_DROIT).setName("Pince module droit");
        pinceModuleDroit
                .addPosition("Ouvert", IConstantesServos.PINCE_MODULE_DROIT_OUVERT)
                .addPosition("Prise produit", IConstantesServos.PINCE_MODULE_DROIT_PRISE_PRODUIT)
                .addPosition("Ventouse", IConstantesServos.PINCE_MODULE_DROIT_CHARGEMENT_VENTOUSE)
                .addPosition("Fermé", IConstantesServos.PINCE_MODULE_DROIT_FERME);
        servoConfigs.add(pinceModuleDroit);

        ServoConfig pinceModuleCentre = new ServoConfig();
        pinceModuleCentre.setId(IConstantesServos.PINCE_MODULE_CENTRE).setName("Pince module centre");
        pinceModuleCentre
                .addPosition("Ouvert dans droit", IConstantesServos.PINCE_MODULE_CENTRE_OUVERT_DANS_DROIT)
                .addPosition("Ouvert", IConstantesServos.PINCE_MODULE_CENTRE_OUVERT)
                .addPosition("Ferme", IConstantesServos.PINCE_MODULE_CENTRE_FERME);

        servoConfigs.add(pinceModuleCentre);

        ServoConfig inclinaisonBras = new ServoConfig();
        inclinaisonBras.setId(IConstantesServos.INCLINAISON_BRAS).setName("Inclinaison bras");
        inclinaisonBras
                .addPosition("Prise robot", IConstantesServos.INCLINAISON_BRAS_PRISE_ROBOT)
                .addPosition("Prise fusée", IConstantesServos.INCLINAISON_BRAS_PRISE_FUSEE)
                .addPosition("Attente", IConstantesServos.INCLINAISON_BRAS_ATTENTE)
                .addPosition("Depose", IConstantesServos.INCLINAISON_BRAS_DEPOSE)
                .addPosition("Vertical", IConstantesServos.INCLINAISON_BRAS_VERTICAL);
        servoConfigs.add(inclinaisonBras);

        ServoConfig rotationVentouse = new ServoConfig();
        rotationVentouse.setId(IConstantesServos.ROTATION_VENTOUSE).setName("Rotation ventouse");
        rotationVentouse
                .addPosition("Prise robot", IConstantesServos.ROTATION_VENTOUSE_PRISE_ROBOT)
                .addPosition("Prise fusée", IConstantesServos.ROTATION_VENTOUSE_PRISE_FUSEE)
                .addPosition("Dépose", IConstantesServos.ROTATION_VENTOUSE_DEPOSE_MAGASIN);
        servoConfigs.add(rotationVentouse);

        ServoConfig porteMagasinDroit = new ServoConfig();
        porteMagasinDroit.setId(IConstantesServos.PORTE_MAGASIN_DROIT).setName("Porte magasin droit");
        porteMagasinDroit
                .addPosition("Ouvert", IConstantesServos.PORTE_DROITE_OUVERT)
                .addPosition("Fermé", IConstantesServos.PORTE_DROITE_FERME);
        servoConfigs.add(porteMagasinDroit);

        ServoConfig porteMagasinGauche = new ServoConfig();
        porteMagasinGauche.setId(IConstantesServos.PORTE_MAGASIN_GAUCHE).setName("Porte magasin gauche");
        porteMagasinGauche
                .addPosition("Ouvert", IConstantesServos.PORTE_GAUCHE_OUVERT)
                .addPosition("Fermé", IConstantesServos.PORTE_GAUCHE_FERME);
        servoConfigs.add(porteMagasinGauche);

        ServoConfig blocageEntreeMagasin = new ServoConfig();
        blocageEntreeMagasin.setId(IConstantesServos.BLOCAGE_ENTREE_MAG).setName("Blocage entrée magasin");
        blocageEntreeMagasin
                .addPosition("Ouvert", IConstantesServos.BLOCAGE_OUVERT)
                .addPosition("Ferme", IConstantesServos.BLOCAGE_FERME);
        servoConfigs.add(blocageEntreeMagasin);

        ServoConfig devidoirMagasin = new ServoConfig();
        devidoirMagasin.setId(IConstantesServos.DEVIDOIR).setName("Devidoir magasin");
        devidoirMagasin
                .addPosition("Chargement", IConstantesServos.DEVIDOIR_CHARGEMENT)
                .addPosition("Déchargement", IConstantesServos.DEVIDOIR_DECHARGEMENT)
                .addPosition("Lecture couleur", IConstantesServos.DEVIDOIR_LECTURE_COULEUR);
        servoConfigs.add(devidoirMagasin);

        ServoConfig orientationAspiration = new ServoConfig();
        orientationAspiration.setId(IConstantesServos.INCLINAISON_ASPIRATION).setName("Inclinaison aspiration");
        orientationAspiration
                .addPosition("Init calage", IConstantesServos.INCLINAISON_ASPI_INIT_CALAGE)
                .addPosition("Transfert", IConstantesServos.INCLINAISON_ASPI_TRANSFERT)
                .addPosition("Cratère", IConstantesServos.INCLINAISON_ASPI_CRATERE)
                .addPosition("Fermé", IConstantesServos.INCLINAISON_ASPI_FERME);
        servoConfigs.add(orientationAspiration);

        // Temp
        ServoConfig moteurRouleaux = new ServoConfig();
        moteurRouleaux.setId(IConstantesServos.MOTOR_ROULEAUX).setName("Moteur rouleaux");
        moteurRouleaux
                .addPosition("Reverse Full", IConstantesServos.MOTOR_REVERSE_FULL)
                .addPosition("Reverse Medium", IConstantesServos.MOTOR_REVERSE_MEDIUM)
                .addPosition("Stop", IConstantesServos.MOTOR_STOP)
                .addPosition("Forward Medium", IConstantesServos.MOTOR_FORWARD_MEDIUM)
                .addPosition("Forward Full", IConstantesServos.MOTOR_FORWARD_FULL);

        servoConfigs.add(moteurRouleaux);

        ServoConfig moteurEjection = new ServoConfig();
        moteurEjection.setId(IConstantesServos.MOTOR_EJECTION).setName("Moteur ejection");
        moteurEjection
                .addPosition("Reverse Full", IConstantesServos.MOTOR_REVERSE_FULL)
                .addPosition("Reverse Medium", IConstantesServos.MOTOR_REVERSE_MEDIUM)
                .addPosition("Stop", IConstantesServos.MOTOR_STOP)
                .addPosition("Forward Medium", IConstantesServos.MOTOR_FORWARD_MEDIUM)
                .addPosition("Forward Full", IConstantesServos.MOTOR_FORWARD_FULL);
        servoConfigs.add(moteurEjection);

        ServoConfig moteurAspiration = new ServoConfig();
        moteurAspiration.setId(IConstantesServos.MOTOR_ASPIRATION).setName("Moteur aspiration");
        moteurAspiration
                .addPosition("Stop", IConstantesServos.MOTOR_ASPIRATION_STOP)
                .addPosition("A Fond", IConstantesServos.MOTOR_ASPIRATION_FULL);
        servoConfigs.add(moteurAspiration);

        /*
        ServoConfig moteurDroit = new ServoConfig();
        moteurDroit.setId(IConstantesServos.MOTOR_DROIT).setName("/!\\ Moteur droit");
        moteurDroit
                .addPosition("Reverse", 1450)
                .addPosition("Stop", 1500)
                .addPosition("Forward", 1650);
        servoConfigs.add(moteurDroit);

        ServoConfig moteurGauche = new ServoConfig();
        moteurGauche.setId(IConstantesServos.MOTOR_GAUCHE).setName("/!\\ Moteur gauche");
        moteurGauche
                .addPosition("Reverse", 1450)
                .addPosition("Stop", 1500)
                .addPosition("Forward", 1650);
        servoConfigs.add(moteurGauche);
        */
    }

    @Override
    protected List<ServoConfig> servosConfig() {
        servoConfigs.forEach(sc -> {
            sc.setCurrentPosition(sd21Servos.getPosition(sc.getId()));
            sc.setCurrentSpeed(sd21Servos.getSpeed(sc.getId()));
        });

        return servoConfigs;
    }
}
