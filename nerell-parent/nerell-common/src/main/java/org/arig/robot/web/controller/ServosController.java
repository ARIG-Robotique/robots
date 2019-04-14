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
        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.PINCE_SERRAGE_PALET_DROIT)
                .name("Pince serrage droit")
                .position("Ferme", IConstantesServos.PINCE_SERRAGE_PALET_DROIT_FERME)
                .position("Lock", IConstantesServos.PINCE_SERRAGE_PALET_DROIT_LOCK)
                .position("Ouvert", IConstantesServos.PINCE_SERRAGE_PALET_DROIT_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE)
                .name("Pince serrage gauche")
                .position("Ferme", IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_FERME)
                .position("Lock", IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_LOCK)
                .position("Ouvert", IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.PIVOT_VENTOUSE_DROIT)
                .name("Pivot ventouse droit")
                .position("Carousel", IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL)
                .position("Sortie carousel", IConstantesServos.PIVOT_VENTOUSE_DROIT_SORTIE_CAROUSEL)
                .position("Facade", IConstantesServos.PIVOT_VENTOUSE_DROIT_FACADE)
                .position("Table", IConstantesServos.PIVOT_VENTOUSE_DROIT_TABLE)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.PIVOT_VENTOUSE_GAUCHE)
                .name("Pivot ventouse gauche")
                .position("Carousel", IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL)
                .position("Sortie carousel", IConstantesServos.PIVOT_VENTOUSE_GAUCHE_SORTIE_CAROUSEL)
                .position("Facade", IConstantesServos.PIVOT_VENTOUSE_GAUCHE_FACADE)
                .position("Table", IConstantesServos.PIVOT_VENTOUSE_GAUCHE_TABLE)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT)
                .name("Ascenseur droit")
                .position("Pre accelerateur", IConstantesServos.ASCENSEUR_DROIT_PRE_ACCELERATEUR)
                .position("Carousel", IConstantesServos.ASCENSEUR_DROIT_CAROUSEL)
                .position("Accelerateur", IConstantesServos.ASCENSEUR_DROIT_ACCELERATEUR)
                .position("Distributeur", IConstantesServos.ASCENSEUR_DROIT_DISTRIBUTEUR)
                .position("Table", IConstantesServos.ASCENSEUR_DROIT_TABLE)
                .position("Table pour gold", IConstantesServos.ASCENSEUR_DROIT_TABLE_GOLD)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE)
                .name("Ascenseur gauche")
                .position("Pre accelerateur", IConstantesServos.ASCENSEUR_GAUCHE_PRE_ACCELERATEUR)
                .position("Carousel", IConstantesServos.ASCENSEUR_GAUCHE_CAROUSEL)
                .position("Accelerateur", IConstantesServos.ASCENSEUR_GAUCHE_ACCELERATEUR)
                .position("Distributeur", IConstantesServos.ASCENSEUR_GAUCHE_DISTRIBUTEUR)
                .position("Table", IConstantesServos.ASCENSEUR_GAUCHE_TABLE)
                .position("Table pour gold", IConstantesServos.ASCENSEUR_GAUCHE_TABLE_GOLD)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.PORTE_BARILLET_DROIT)
                .name("Porte barillet droit")
                .position("Ferme", IConstantesServos.PORTE_BARILLET_DROIT_FERME)
                .position("Ouvert", IConstantesServos.PORTE_BARILLET_DROIT_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.PORTE_BARILLET_GAUCHE)
                .name("Porte barillet gauche")
                .position("Ferme", IConstantesServos.PORTE_BARILLET_GAUCHE_FERME)
                .position("Ouvert", IConstantesServos.PORTE_BARILLET_GAUCHE_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.TRAPPE_MAGASIN_DROIT)
                .name("Trappe magasin gauche")
                .position("Ferme", IConstantesServos.TRAPPE_MAGASIN_DROIT_FERME)
                .position("Ouvert", IConstantesServos.TRAPPE_MAGASIN_DROIT_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.TRAPPE_MAGASIN_GAUCHE)
                .name("Trappe magasin gauche")
                .position("Ferme", IConstantesServos.TRAPPE_MAGASIN_GAUCHE_FERME)
                .position("Ouvert", IConstantesServos.TRAPPE_MAGASIN_GAUCHE_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.EJECTION_MAGASIN_DROIT)
                .name("Ejection magasin gauche")
                .position("Ferme", IConstantesServos.EJECTION_MAGASIN_DROIT_FERME)
                .position("Ouvert", IConstantesServos.EJECTION_MAGASIN_DROIT_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.EJECTION_MAGASIN_GAUCHE)
                .name("Ejection magasin gauche")
                .position("Ferme", IConstantesServos.EJECTION_MAGASIN_GAUCHE_FERME)
                .position("Ouvert", IConstantesServos.EJECTION_MAGASIN_GAUCHE_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.POUSSE_ACCELERATEUR_DROIT)
                .name("Pousse accelerateur droit")
                .position("Ferme", IConstantesServos.POUSSE_ACCELERATEUR_DROIT_FERME)
                .position("Standby", IConstantesServos.POUSSE_ACCELERATEUR_DROIT_STANDBY)
                .position("Action", IConstantesServos.POUSSE_ACCELERATEUR_DROIT_ACTION)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE)
                .name("Pousse accelerateur gauche")
                .position("Ferme", IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_FERME)
                .position("Standby", IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_STANDBY)
                .position("Action", IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_ACTION)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.MOTOR_CAROUSEL)
                .name("Moteur carousel")
                .position("Reverse", 1350)
                .position("Stop", 1500)
                .position("Forward", 1650)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.MOTOR_DROIT)
                .name("/!\\ Moteur droit")
                .position("Reverse", 1350)
                .position("Stop", 1500)
                .position("Forward", 1650)
        );

        servoConfigs.add(new ServoConfig()
                .id(IConstantesServos.MOTOR_GAUCHE)
                .name("/!\\ Moteur gauche")
                .position("Reverse", 1350)
                .position("Stop", 1500)
                .position("Forward", 1650)
        );
    }

    @Override
    protected List<ServoConfig> servosConfig() {
        servoConfigs.forEach(sc -> {
            sc.currentPosition(sd21Servos.getPosition(sc.id()));
            sc.currentSpeed(sd21Servos.getSpeed(sc.id()));
        });

        return servoConfigs;
    }
}
