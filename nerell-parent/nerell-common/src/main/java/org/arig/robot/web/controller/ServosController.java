package org.arig.robot.web.controller;

import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.model.servos.ServoConfig;
import org.arig.robot.model.servos.ServoGroup;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServosController extends AbstractServosController {

    private static List<ServoGroup> servoConfigs = new ArrayList<>();

    static {
        servoConfigs.add(new ServoGroup()
                .setName("Autres")
                .servo(new ServoConfig()
                        .setId(IConstantesServos.MOUSTACHE_DROITE)
                        .setName("Moustache droite")
                        .position("Ouvert", IConstantesServos.POS_MOUSTACHE_DROITE_OUVERT)
                        .position("Poussette", IConstantesServos.POS_MOUSTACHE_DROITE_POUSSETTE)
                        .position("Fermé", IConstantesServos.POS_MOUSTACHE_DROITE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.MOUSTACHE_GAUCHE)
                        .setName("Moustache gauche")
                        .position("Ouvert", IConstantesServos.POS_MOUSTACHE_GAUCHE_OUVERT)
                        .position("Poussette", IConstantesServos.POS_MOUSTACHE_GAUCHE_POUSSETTE)
                        .position("Fermé", IConstantesServos.POS_MOUSTACHE_GAUCHE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.BRAS_DROITE)
                        .setName("Poussoir droite")
                        .position("Ouvert", IConstantesServos.POS_BRAS_DROITE_MANCHE_AIR)
                        .position("Fermé", IConstantesServos.POS_BRAS_DROITE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.BRAS_GAUCHE)
                        .setName("Poussoir gauche")
                        .position("Ouvert", IConstantesServos.POS_BRAS_GAUCHE_MANCHE_AIR)
                        .position("Fermé", IConstantesServos.POS_BRAS_GAUCHE_FERME)
                )
        );

        servoConfigs.add(new ServoGroup()
                .setId(IConstantesServos.GROUPE_PINCES_ARRIERE)
                .setName("Pinces arrières")
                .batch("Tout ouvert", IConstantesServos.POS_GROUPE_PINCES_ARRIERE_OUVERT)
                .batch("Tout fermé", IConstantesServos.POS_GROUPE_PINCES_ARRIERE_FERME)
                .servo(new ServoConfig()
                        .setId(IConstantesServos.ASCENSEUR_ARRIERE)
                        .setName("Ascenseur arrière")
                        .position("Haut", IConstantesServos.POS_ASCENSEUR_ARRIERE_HAUT)
                        .position("Eccueil", IConstantesServos.POS_ASCENSEUR_ARRIERE_ECCUEIL)
                        .position("Table", IConstantesServos.POS_ASCENSEUR_ARRIERE_TABLE)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PIVOT_ARRIERE)
                        .setName("Pivot arrière")
                        .position("Ouvert", IConstantesServos.POS_PIVOT_ARRIERE_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PIVOT_ARRIERE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PINCE_ARRIERE_1)
                        .setName("Pince arrière 1")
                        .position("Ouvert", IConstantesServos.POS_PINCE_ARRIERE_1_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PINCE_ARRIERE_1_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PINCE_ARRIERE_2)
                        .setName("Pince arrière 2")
                        .position("Ouvert", IConstantesServos.POS_PINCE_ARRIERE_2_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PINCE_ARRIERE_2_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PINCE_ARRIERE_3)
                        .setName("Pince arrière 3")
                        .position("Ouvert", IConstantesServos.POS_PINCE_ARRIERE_3_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PINCE_ARRIERE_3_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PINCE_ARRIERE_4)
                        .setName("Pince arrière 4")
                        .position("Ouvert", IConstantesServos.POS_PINCE_ARRIERE_4_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PINCE_ARRIERE_4_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PINCE_ARRIERE_5)
                        .setName("Pince arrière 5")
                        .position("Ouvert", IConstantesServos.POS_PINCE_ARRIERE_5_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PINCE_ARRIERE_5_FERME)
                )
        );

        servoConfigs.add(new ServoGroup()
                .setId(IConstantesServos.GROUPE_PINCES_AVANT)
                .setName("Pinces avants")
                .batch("Tout ouvert", IConstantesServos.POS_GROUPE_PINCES_AVANT_OUVERT)
                .batch("Tout fermé", IConstantesServos.POS_GROUPE_PINCES_AVANT_FERME)
                .servo(new ServoConfig()
                        .setId(IConstantesServos.ASCENSEUR_AVANT)
                        .setName("Ascenseur avant")
                        .position("Haut", IConstantesServos.POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE)
                        .position("Bas", IConstantesServos.POS_ASCENSEUR_AVANT_BAS)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PINCE_AVANT_1)
                        .setName("Pince avant 1")
                        .position("Ouvert", IConstantesServos.POS_PINCE_AVANT_1_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PINCE_AVANT_1_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PINCE_AVANT_2)
                        .setName("Pince avant 2")
                        .position("Ouvert", IConstantesServos.POS_PINCE_AVANT_2_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PINCE_AVANT_2_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PINCE_AVANT_3)
                        .setName("Pince avant 3")
                        .position("Ouvert", IConstantesServos.POS_PINCE_AVANT_3_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PINCE_AVANT_3_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServos.PINCE_AVANT_4)
                        .setName("Pince avant 4")
                        .position("Ouvert", IConstantesServos.POS_PINCE_AVANT_4_OUVERT)
                        .position("Fermé", IConstantesServos.POS_PINCE_AVANT_4_FERME)
                )
        );
    }

    @Override
    protected int[][] getGroupPositions(Byte idGroupe, Byte position) {
        try {
            return IConstantesServos.GROUP_CONFIG.get(idGroupe).get(position);
        } catch (Exception e) {
            return new int[][]{};
        }
    }

    @Override
    protected List<ServoGroup> servosConfig() {
        servoConfigs.forEach(sc -> {
            sc.getServos().forEach(s -> {
                s.setCurrentPosition(sd21Servos.getPosition(s.getId()));
                s.setCurrentSpeed(sd21Servos.getSpeed(s.getId()));
            });
        });

        return servoConfigs;
    }
}
