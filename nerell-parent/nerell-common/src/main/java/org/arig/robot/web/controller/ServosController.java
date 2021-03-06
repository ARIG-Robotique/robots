package org.arig.robot.web.controller;

import org.arig.robot.constants.IConstantesServosNerell;
import org.arig.robot.model.servos.ServoConfig;
import org.arig.robot.model.servos.ServoGroup;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServosController extends AbstractServosController {

    private static final String OUVERT = "Ouvert";
    private static final String FERME = "Fermé";
    private static final String POUSSETTE = "Poussette";
    private static final String MANCHE_A_AIR = "Manche à air";
    private static final String PHARE = "Phare";
    private static final String PRISE = "Prise";

    private static List<ServoGroup> servoConfigs = new ArrayList<>();

    static {
        servoConfigs.add(new ServoGroup()
                .setName("Autres")
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.MOUSTACHE_DROITE)
                        .setName("Moustache droite")
                        .position(OUVERT, IConstantesServosNerell.POS_MOUSTACHE_DROITE_OUVERT)
                        .position(POUSSETTE, IConstantesServosNerell.POS_MOUSTACHE_DROITE_POUSSETTE)
                        .position(FERME, IConstantesServosNerell.POS_MOUSTACHE_DROITE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.MOUSTACHE_GAUCHE)
                        .setName("Moustache gauche")
                        .position(OUVERT, IConstantesServosNerell.POS_MOUSTACHE_GAUCHE_OUVERT)
                        .position(POUSSETTE, IConstantesServosNerell.POS_MOUSTACHE_GAUCHE_POUSSETTE)
                        .position(FERME, IConstantesServosNerell.POS_MOUSTACHE_GAUCHE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.BRAS_DROIT)
                        .setName("Bras droit")
                        .position(MANCHE_A_AIR, IConstantesServosNerell.POS_BRAS_DROIT_MANCHE_AIR)
                        .position(PHARE, IConstantesServosNerell.POS_BRAS_DROIT_PHARE)
                        .position(FERME, IConstantesServosNerell.POS_BRAS_DROIT_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.BRAS_GAUCHE)
                        .setName("Bras gauche")
                        .position(MANCHE_A_AIR, IConstantesServosNerell.POS_BRAS_GAUCHE_MANCHE_AIR)
                        .position(PHARE, IConstantesServosNerell.POS_BRAS_GAUCHE_PHARE)
                        .position(FERME, IConstantesServosNerell.POS_BRAS_GAUCHE_FERME)
                )
        );

        servoConfigs.add(new ServoGroup()
                .setId(IConstantesServosNerell.BATCH_PINCES_ARRIERE)
                .setName("Pinces arrières")
                .batch("Tout ouvert", IConstantesServosNerell.POS_BATCH_PINCES_ARRIERE_OUVERT)
                .batch("Tout fermé", IConstantesServosNerell.POS_BATCH_PINCES_ARRIERE_FERME)
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.ASCENSEUR_ARRIERE)
                        .setName("Ascenseur arrière")
                        .position("Haut", IConstantesServosNerell.POS_ASCENSEUR_ARRIERE_HAUT)
                        .position("Ecueil", IConstantesServosNerell.POS_ASCENSEUR_ARRIERE_ECUEIL)
                        .position("Table", IConstantesServosNerell.POS_ASCENSEUR_ARRIERE_TABLE)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PIVOT_ARRIERE)
                        .setName("Pivot arrière")
                        .position(OUVERT, IConstantesServosNerell.POS_PIVOT_ARRIERE_OUVERT)
                        .position(FERME, IConstantesServosNerell.POS_PIVOT_ARRIERE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PINCE_ARRIERE_1)
                        .setName("Pince arrière 1")
                        .position(OUVERT, IConstantesServosNerell.POS_PINCE_ARRIERE_1_OUVERT)
                        .position(FERME, IConstantesServosNerell.POS_PINCE_ARRIERE_1_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PINCE_ARRIERE_2)
                        .setName("Pince arrière 2")
                        .position(OUVERT, IConstantesServosNerell.POS_PINCE_ARRIERE_2_OUVERT)
                        .position(FERME, IConstantesServosNerell.POS_PINCE_ARRIERE_2_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PINCE_ARRIERE_3)
                        .setName("Pince arrière 3")
                        .position(OUVERT, IConstantesServosNerell.POS_PINCE_ARRIERE_3_OUVERT)
                        .position(FERME, IConstantesServosNerell.POS_PINCE_ARRIERE_3_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PINCE_ARRIERE_4)
                        .setName("Pince arrière 4")
                        .position(OUVERT, IConstantesServosNerell.POS_PINCE_ARRIERE_4_OUVERT)
                        .position(FERME, IConstantesServosNerell.POS_PINCE_ARRIERE_4_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PINCE_ARRIERE_5)
                        .setName("Pince arrière 5")
                        .position(OUVERT, IConstantesServosNerell.POS_PINCE_ARRIERE_5_OUVERT)
                        .position(FERME, IConstantesServosNerell.POS_PINCE_ARRIERE_5_FERME)
                )
        );

        servoConfigs.add(new ServoGroup()
                .setId(IConstantesServosNerell.BATCH_PINCES_AVANT)
                .setName("Pinces avants")
                .batch("Tout ouvert", IConstantesServosNerell.POS_BATCH_PINCES_AVANT_OUVERT)
                .batch("Tout prise", IConstantesServosNerell.POS_BATCH_PINCES_AVANT_PRISE)
                .batch("Tout fermé", IConstantesServosNerell.POS_BATCH_PINCES_AVANT_FERME)
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.ASCENSEUR_AVANT)
                        .setName("Ascenseur avant")
                        .position("Ouverture Moustache", IConstantesServosNerell.POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE)
                        .position("Roulage", IConstantesServosNerell.POS_ASCENSEUR_AVANT_ROULAGE)
                        .position("Bas", IConstantesServosNerell.POS_ASCENSEUR_AVANT_BAS)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PINCE_AVANT_1)
                        .setName("Pince avant 1")
                        .position(OUVERT, IConstantesServosNerell.POS_PINCE_AVANT_1_OUVERT)
                        .position(PRISE, IConstantesServosNerell.POS_PINCE_AVANT_1_PRISE)
                        .position(FERME, IConstantesServosNerell.POS_PINCE_AVANT_1_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PINCE_AVANT_2)
                        .setName("Pince avant 2")
                        .position(OUVERT, IConstantesServosNerell.POS_PINCE_AVANT_2_OUVERT)
                        .position(PRISE, IConstantesServosNerell.POS_PINCE_AVANT_2_PRISE)
                        .position(FERME, IConstantesServosNerell.POS_PINCE_AVANT_2_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PINCE_AVANT_3)
                        .setName("Pince avant 3")
                        .position(OUVERT, IConstantesServosNerell.POS_PINCE_AVANT_3_OUVERT)
                        .position(PRISE, IConstantesServosNerell.POS_PINCE_AVANT_3_PRISE)
                        .position(FERME, IConstantesServosNerell.POS_PINCE_AVANT_3_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosNerell.PINCE_AVANT_4)
                        .setName("Pince avant 4")
                        .position(OUVERT, IConstantesServosNerell.POS_PINCE_AVANT_4_OUVERT)
                        .position(PRISE, IConstantesServosNerell.POS_PINCE_AVANT_4_PRISE)
                        .position(FERME, IConstantesServosNerell.POS_PINCE_AVANT_4_FERME)
                )
        );
    }

    @Override
    protected int[][] getBatchPositions(Byte idBatch, Byte position) {
        try {
            return IConstantesServosNerell.BATCH_CONFIG.get(idBatch).get(position);
        } catch (Exception e) {
            return new int[][]{};
        }
    }

    @Override
    protected List<ServoGroup> servosConfig() {
        servoConfigs.forEach(sc ->
            sc.getServos().forEach(s -> {
                s.setCurrentPosition(sd21Servos.getPosition(s.getId()));
                s.setCurrentSpeed(sd21Servos.getSpeed(s.getId()));
            })
        );

        return servoConfigs;
    }
}
