package org.arig.robot.web.controller;

import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.model.servos.ServoConfig;
import org.arig.robot.model.servos.ServoPosition;
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
        ServoConfig brasDroit = new ServoConfig();
        brasDroit.setId(IConstantesServos.BRAS_DROIT).setName("Bras droit");
        brasDroit.addPosition(new ServoPosition("Haut", IConstantesServos.BRAS_DROIT_HAUT))
                .addPosition(new ServoPosition("Clap", IConstantesServos.BRAS_DROIT_CLAP))
                .addPosition(new ServoPosition("Bas", IConstantesServos.BRAS_DROIT_BAS));
        servoConfigs.add(brasDroit);

        ServoConfig brasGauche = new ServoConfig();
        brasGauche.setId(IConstantesServos.BRAS_GAUCHE).setName("Bras gauche");
        brasGauche.addPosition(new ServoPosition("Haut", IConstantesServos.BRAS_GAUCHE_HAUT))
                .addPosition(new ServoPosition("Clap", IConstantesServos.BRAS_GAUCHE_CLAP))
                .addPosition(new ServoPosition("Bas", IConstantesServos.BRAS_GAUCHE_BAS));
        servoConfigs.add(brasGauche);

        ServoConfig tapisDroit = new ServoConfig();
        tapisDroit.setId(IConstantesServos.TAPIS_DROIT).setName("Tapis droit");
        tapisDroit.addPosition(new ServoPosition("Ouvert", IConstantesServos.TAPIS_DROIT_OUVERT))
                .addPosition(new ServoPosition("Ferme", IConstantesServos.TAPIS_DROIT_FERME));
        servoConfigs.add(tapisDroit);

        ServoConfig tapisGauche = new ServoConfig();
        tapisGauche.setId(IConstantesServos.TAPIS_GAUCHE).setName("Tapis gauche");
        tapisGauche.addPosition(new ServoPosition("Ouvert", IConstantesServos.TAPIS_GAUCHE_OUVERT))
                .addPosition(new ServoPosition("Ferme", IConstantesServos.TAPIS_GAUCHE_FERME));
        servoConfigs.add(tapisGauche);

        ServoConfig gobeletDroit = new ServoConfig();
        gobeletDroit.setId(IConstantesServos.PRODUIT_DROIT).setName("Pince produit droit");
        gobeletDroit.addPosition(new ServoPosition("Ouvert", IConstantesServos.PRODUIT_DROIT_OUVERT))
                .addPosition(new ServoPosition("Produit", IConstantesServos.PRODUIT_DROIT_FERME))
                .addPosition(new ServoPosition("Init", IConstantesServos.PRODUIT_DROIT_INIT));
        servoConfigs.add(gobeletDroit);

        ServoConfig gobeletGauche = new ServoConfig();
        gobeletGauche.setId(IConstantesServos.PRODUIT_GAUCHE).setName("Pince produit gauche");
        gobeletGauche.addPosition(new ServoPosition("Ouvert", IConstantesServos.PRODUIT_GAUCHE_OUVERT))
                .addPosition(new ServoPosition("Produit", IConstantesServos.PRODUIT_GAUCHE_FERME))
                .addPosition(new ServoPosition("Init", IConstantesServos.PRODUIT_GAUCHE_INIT));
        servoConfigs.add(gobeletGauche);

        ServoConfig monteGobeletDroit = new ServoConfig();
        monteGobeletDroit.setId(IConstantesServos.MONTE_GOBELET_DROIT).setName("Monte gobelet droit");
        monteGobeletDroit.addPosition(new ServoPosition("Haut", IConstantesServos.MONTE_GB_DROIT_HAUT))
                .addPosition(new ServoPosition("Bas", IConstantesServos.MONTE_GB_DROIT_BAS));
        servoConfigs.add(monteGobeletDroit);

        ServoConfig monteGobeletGauche = new ServoConfig();
        monteGobeletGauche.setId(IConstantesServos.MONTE_GOBELET_GAUCHE).setName("Monte gobelet gauche");
        monteGobeletGauche.addPosition(new ServoPosition("Haut", IConstantesServos.MONTE_GB_GAUCHE_HAUT))
                .addPosition(new ServoPosition("Bas", IConstantesServos.MONTE_GB_GAUCHE_BAS));
        servoConfigs.add(monteGobeletGauche);

        ServoConfig ascenseur = new ServoConfig();
        ascenseur.setId(IConstantesServos.ASCENSEUR).setName("Ascenseur");
        ascenseur.addPosition(new ServoPosition("Haut pied", IConstantesServos.ASCENSEUR_HAUT_PIED))
                .addPosition(new ServoPosition("Haut balle", IConstantesServos.ASCENSEUR_HAUT_BALLE))
                .addPosition(new ServoPosition("Plein", IConstantesServos.ASCENSEUR_PLEIN))
                .addPosition(new ServoPosition("Bordure depose", IConstantesServos.ASCENSEUR_DEPOSE_BORDURE))
                .addPosition(new ServoPosition("Bas", IConstantesServos.ASCENSEUR_BAS));
        servoConfigs.add(ascenseur);

        ServoConfig pince = new ServoConfig();
        pince.setId(IConstantesServos.PINCE).setName("Pince");
        pince.addPosition(new ServoPosition("Ouverte", IConstantesServos.PINCE_OUVERTE))
                .addPosition(new ServoPosition("Check couleur", IConstantesServos.PINCE_COULEUR))
                .addPosition(new ServoPosition("Prise balle", IConstantesServos.PINCE_PRISE_BALLE))
                .addPosition(new ServoPosition("Prise pied", IConstantesServos.PINCE_PRISE_PIED));
        servoConfigs.add(pince);

        ServoConfig guide = new ServoConfig();
        guide.setId(IConstantesServos.GUIDE).setName("Guide");
        guide.addPosition(new ServoPosition("Ouvert", IConstantesServos.GUIDE_OUVERT))
                .addPosition(new ServoPosition("Ferme", IConstantesServos.GUIDE_FERME));
        servoConfigs.add(guide);
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
