package org.arig.robot.web.controller;

import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.model.servos.ServoConfig;
import org.arig.robot.model.servos.ServoUtils;
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
                .setId(IConstantesServos.PINCE_SERRAGE_PALET_DROIT)
                .setGroup(ServoUtils.groupImportant())
                .setName("Pince serrage droit")
                .position("Repos", IConstantesServos.PINCE_SERRAGE_PALET_DROIT_REPOS)
                .position("Lock", IConstantesServos.PINCE_SERRAGE_PALET_DROIT_LOCK)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE)
                .setGroup(ServoUtils.groupImportant())
                .setName("Pince serrage gauche")
                .position("Repos", IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_REPOS)
                .position("Lock", IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_LOCK)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.PIVOT_VENTOUSE_DROIT)
                .setName("Pivot ventouse droit")
                .setGroup(ServoUtils.groupImportant())
                .position("Carousel vertical", IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL_VERTICAL)
                .position("Carousel sortie", IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL_SORTIE)
                .position("Facade", IConstantesServos.PIVOT_VENTOUSE_DROIT_FACADE)
                .position("Vomi", IConstantesServos.PIVOT_VENTOUSE_DROIT_VOMI)
                .position("Table", IConstantesServos.PIVOT_VENTOUSE_DROIT_TABLE)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.PIVOT_VENTOUSE_GAUCHE)
                .setName("Pivot ventouse gauche")
                .setGroup(ServoUtils.groupImportant())
                .position("Carousel vertical", IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL_VERTICAL)
                .position("Carousel sortie", IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL_SORTIE)
                .position("Facade", IConstantesServos.PIVOT_VENTOUSE_GAUCHE_FACADE)
                .position("Vomi", IConstantesServos.PIVOT_VENTOUSE_GAUCHE_VOMI)
                .position("Table", IConstantesServos.PIVOT_VENTOUSE_GAUCHE_TABLE)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT)
                .setName("Ascenseur droit")
                .setGroup(ServoUtils.groupImportant())
                .position("Carousel dépose", IConstantesServos.ASCENSEUR_DROIT_CAROUSEL_DEPOSE)
                .position("Carousel", IConstantesServos.ASCENSEUR_DROIT_CAROUSEL)
                .position("Accelerateur", IConstantesServos.ASCENSEUR_DROIT_ACCELERATEUR)
                .position("Accelerateur dépose", IConstantesServos.ASCENSEUR_DROIT_ACCELERATEUR_DEPOSE)
                .position("Distributeur", IConstantesServos.ASCENSEUR_DROIT_DISTRIBUTEUR)
                .position("Distributeur gold", IConstantesServos.ASCENSEUR_DROIT_GOLD)
                .position("Table", IConstantesServos.ASCENSEUR_DROIT_TABLE)
                .position("Table pour gold", IConstantesServos.ASCENSEUR_DROIT_TABLE_GOLD)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE)
                .setName("Ascenseur gauche")
                .setGroup(ServoUtils.groupImportant())
                .position("Carousel dépose", IConstantesServos.ASCENSEUR_GAUCHE_CAROUSEL_DEPOSE)
                .position("Carousel", IConstantesServos.ASCENSEUR_GAUCHE_CAROUSEL)
                .position("Accelerateur", IConstantesServos.ASCENSEUR_GAUCHE_ACCELERATEUR)
                .position("Accelerateur dépose", IConstantesServos.ASCENSEUR_GAUCHE_ACCELERATEUR_DEPOSE)
                .position("Distributeur", IConstantesServos.ASCENSEUR_GAUCHE_DISTRIBUTEUR)
                .position("Distributeur gold", IConstantesServos.ASCENSEUR_GAUCHE_GOLD)
                .position("Table", IConstantesServos.ASCENSEUR_GAUCHE_TABLE)
                .position("Table pour gold", IConstantesServos.ASCENSEUR_GAUCHE_TABLE_GOLD)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.PORTE_BARILLET_DROIT)
                .setName("Porte barillet droit")
                .setGroup(ServoUtils.groupImportant())
                .position("Ferme", IConstantesServos.PORTE_BARILLET_DROIT_FERME)
                .position("Ouvert", IConstantesServos.PORTE_BARILLET_DROIT_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.PORTE_BARILLET_GAUCHE)
                .setName("Porte barillet gauche")
                .setGroup(ServoUtils.groupImportant())
                .position("Ferme", IConstantesServos.PORTE_BARILLET_GAUCHE_FERME)
                .position("Ouvert", IConstantesServos.PORTE_BARILLET_GAUCHE_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.TRAPPE_MAGASIN_DROIT)
                .setName("Trappe magasin droit")
                .setGroup(ServoUtils.groupDerier())
                .position("Ferme", IConstantesServos.TRAPPE_MAGASIN_DROIT_FERME)
                .position("Ouvert", IConstantesServos.TRAPPE_MAGASIN_DROIT_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.TRAPPE_MAGASIN_GAUCHE)
                .setName("Trappe magasin gauche")
                .setGroup(ServoUtils.groupDerier())
                .position("Ferme", IConstantesServos.TRAPPE_MAGASIN_GAUCHE_FERME)
                .position("Ouvert", IConstantesServos.TRAPPE_MAGASIN_GAUCHE_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.EJECTION_MAGASIN_DROIT)
                .setName("Ejection magasin droit")
                .setGroup(ServoUtils.groupDivers())
                .position("Ferme", IConstantesServos.EJECTION_MAGASIN_DROIT_FERME)
                .position("Ouvert", IConstantesServos.EJECTION_MAGASIN_DROIT_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.EJECTION_MAGASIN_GAUCHE)
                .setName("Ejection magasin gauche")
                .setGroup(ServoUtils.groupDivers())
                .position("Ferme", IConstantesServos.EJECTION_MAGASIN_GAUCHE_FERME)
                .position("Ouvert", IConstantesServos.EJECTION_MAGASIN_GAUCHE_OUVERT)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.POUSSE_ACCELERATEUR_DROIT)
                .setName("Pousse accelerateur droit")
                .setGroup(ServoUtils.groupDivers())
                .position("Ferme", IConstantesServos.POUSSE_ACCELERATEUR_DROIT_FERME)
                .position("Standby", IConstantesServos.POUSSE_ACCELERATEUR_DROIT_STANDBY)
                .position("Action", IConstantesServos.POUSSE_ACCELERATEUR_DROIT_ACTION)
        );

        servoConfigs.add(new ServoConfig()
                .setId(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE)
                .setName("Pousse accelerateur gauche")
                .setGroup(ServoUtils.groupDivers())
                .position("Ferme", IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_FERME)
                .position("Standby", IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_STANDBY)
                .position("Action", IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_ACTION)
        );
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
