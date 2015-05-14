package org.arig.eurobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.constants.IConstantesSpringConfig;
import org.arig.eurobot.model.servos.ServoDTO;
import org.arig.eurobot.model.servos.ServoPositionDTO;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mythril on 21/12/13.
 */
@Slf4j
@Profile(IConstantesSpringConfig.profileMonitoring)
@RestController
@RequestMapping("/servos")
public class ServosController {

    @Autowired
    private SD21Servos sd21Servos;

    @RequestMapping
    public List<ServoDTO> list() {
        List<ServoDTO> res = new LinkedList<>();

        ServoDTO brasDroit = new ServoDTO();
        brasDroit.setId(IConstantesServos.BRAS_DROIT).setSpeed(IConstantesServos.SPEED_BRAS)
                .setName("Bras droit")
                .addPosition(new ServoPositionDTO("Haut", IConstantesServos.BRAS_DROIT_HAUT))
                .addPosition(new ServoPositionDTO("Clap", IConstantesServos.BRAS_DROIT_CLAP))
                .addPosition(new ServoPositionDTO("Bas", IConstantesServos.BRAS_DROIT_BAS));
        res.add(brasDroit);

        ServoDTO brasGauche = new ServoDTO();
        brasGauche.setId(IConstantesServos.BRAS_GAUCHE).setSpeed(IConstantesServos.SPEED_BRAS)
                .setName("Bras gauche")
                .addPosition(new ServoPositionDTO("Haut", IConstantesServos.BRAS_GAUCHE_HAUT))
                .addPosition(new ServoPositionDTO("Clap", IConstantesServos.BRAS_GAUCHE_CLAP))
                .addPosition(new ServoPositionDTO("Bas", IConstantesServos.BRAS_GAUCHE_BAS));
        res.add(brasGauche);

        ServoDTO tapisDroit = new ServoDTO();
        tapisDroit.setId(IConstantesServos.TAPIS_DROIT).setSpeed(IConstantesServos.SPEED_TAPIS)
                .setName("Tapis droit")
                .addPosition(new ServoPositionDTO("Ouvert", IConstantesServos.TAPIS_DROIT_OUVERT))
                .addPosition(new ServoPositionDTO("Ferme", IConstantesServos.TAPIS_DROIT_FERME));
        res.add(tapisDroit);

        ServoDTO tapisGauche = new ServoDTO();
        tapisGauche.setId(IConstantesServos.TAPIS_GAUCHE).setSpeed(IConstantesServos.SPEED_TAPIS)
                .setName("Tapis gauche")
                .addPosition(new ServoPositionDTO("Ouvert", IConstantesServos.TAPIS_GAUCHE_OUVERT))
                .addPosition(new ServoPositionDTO("Ferme", IConstantesServos.TAPIS_GAUCHE_FERME));
        res.add(tapisGauche);

        ServoDTO gobeletDroit = new ServoDTO();
        gobeletDroit.setId(IConstantesServos.GOBELET_DROIT).setSpeed(IConstantesServos.SPEED_GOBELET)
                .setName("Pince produit droit")
                .addPosition(new ServoPositionDTO("Ouvert", IConstantesServos.GOBELET_DROIT_OUVERT))
                .addPosition(new ServoPositionDTO("Produit", IConstantesServos.GOBELET_DROIT_PRODUIT))
                .addPosition(new ServoPositionDTO("Init", IConstantesServos.GOBELET_DROIT_INIT));
        res.add(gobeletDroit);

        ServoDTO gobeletGauche = new ServoDTO();
        gobeletGauche.setId(IConstantesServos.GOBELET_GAUCHE).setSpeed(IConstantesServos.SPEED_GOBELET)
                .setName("Pince produit gauche")
                .addPosition(new ServoPositionDTO("Ouvert", IConstantesServos.GOBELET_GAUCHE_OUVERT))
                .addPosition(new ServoPositionDTO("Produit", IConstantesServos.GOBELET_GAUCHE_PRODUIT))
                .addPosition(new ServoPositionDTO("Init", IConstantesServos.GOBELET_GAUCHE_INIT));
        res.add(gobeletGauche);

        ServoDTO monteGobeletDroit = new ServoDTO();
        monteGobeletDroit.setId(IConstantesServos.MONTE_GOBELET_DROIT).setSpeed(IConstantesServos.SPEED_MONTE_GOBELET)
                .setName("Monte gobelet droit")
                .addPosition(new ServoPositionDTO("Haut", IConstantesServos.MONTE_GB_DROIT_HAUT))
                .addPosition(new ServoPositionDTO("Bas", IConstantesServos.MONTE_GB_DROIT_BAS));
        res.add(monteGobeletDroit);

        ServoDTO monteGobeletGauche = new ServoDTO();
        monteGobeletGauche.setId(IConstantesServos.MONTE_GOBELET_GAUCHE).setSpeed(IConstantesServos.SPEED_MONTE_GOBELET)
                .setName("Monte gobelet gauche")
                .addPosition(new ServoPositionDTO("Haut", IConstantesServos.MONTE_GB_GAUCHE_HAUT))
                .addPosition(new ServoPositionDTO("Bas", IConstantesServos.MONTE_GB_GAUCHE_BAS));
        res.add(monteGobeletGauche);

        ServoDTO ascenseur = new ServoDTO();
        ascenseur.setId(IConstantesServos.ASCENSEUR).setSpeed(IConstantesServos.SPEED_ASCENSEUR)
                .setName("Ascenseur")
                .addPosition(new ServoPositionDTO("Haut pied", IConstantesServos.ASCENSEUR_HAUT_PIED))
                .addPosition(new ServoPositionDTO("Haut balle", IConstantesServos.ASCENSEUR_HAUT_BALLE))
                .addPosition(new ServoPositionDTO("Plein", IConstantesServos.ASCENSEUR_PLEIN))
                .addPosition(new ServoPositionDTO("Bordure depose", IConstantesServos.ASCENSEUR_DEPOSE_BORDURE))
                .addPosition(new ServoPositionDTO("Bas", IConstantesServos.ASCENSEUR_BAS));
        res.add(ascenseur);

        ServoDTO pince = new ServoDTO();
        pince.setId(IConstantesServos.PINCE).setSpeed(IConstantesServos.SPEED_PINCE)
                .setName("Pince")
                .addPosition(new ServoPositionDTO("Ouverte", IConstantesServos.PINCE_OUVERTE))
                .addPosition(new ServoPositionDTO("Check couleur", IConstantesServos.PINCE_COULEUR))
                .addPosition(new ServoPositionDTO("Prise balle", IConstantesServos.PINCE_PRISE_BALLE))
                .addPosition(new ServoPositionDTO("Prise pied", IConstantesServos.PINCE_PRISE_PIED));
        res.add(pince);

        ServoDTO guide = new ServoDTO();
        guide.setId(IConstantesServos.GUIDE).setSpeed(IConstantesServos.SPEED_GUIDE)
                .setName("Guide")
                .addPosition(new ServoPositionDTO("Ouvert", IConstantesServos.GUIDE_OUVERT))
                .addPosition(new ServoPositionDTO("Ferme", IConstantesServos.GUIDE_FERME));
        res.add(guide);

        return res;
    }

    @RequestMapping("/{idServo}")
    public void servosPositionAndSpeed(
            @PathVariable("idServo") final Byte idServo,
            @RequestParam("position") final Integer position,
            @RequestParam(value = "speed", required = false) final Byte speed) {

        if (speed != null) {
            log.info("Modification du servo moteur {} : Pos -> {} ; Speed -> {}", idServo, position, speed);
            sd21Servos.setPositionAndSpeed(idServo, position, speed);
        } else {
            log.info("Modification du servo moteur {} : Pos -> {}", idServo, position);
            sd21Servos.setPosition(idServo, position);
        }
    }
}
