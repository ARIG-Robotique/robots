package org.arig.robot.web.controller;

import org.arig.robot.constants.INerellConstantesServos;
import org.arig.robot.model.servos.ServoConfig;
import org.arig.robot.model.servos.ServoGroup;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class NerellServosController extends AbstractServosController {

    private static final String OUVERT = "Ouvert";
    private static final String OUVERT_SPECIAL = "Ouvert Spé";
    private static final String FERME = "Fermé";
    private static final String FERME_HAUT_FOND = "Fermé haut fond";
    private static final String POUSSETTE = "Poussette";
    private static final String MANCHE_A_AIR = "Manche à air";
    private static final String PHARE = "Phare";
    private static final String BAS = "Bas";
    private static final String HAUT = "Haut";

    private static List<ServoGroup> servoConfigs = new ArrayList<>();

    static {
        servoConfigs.add(new ServoGroup()
                .setName("Autres")
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.BRAS_DROIT)
                        .setName("Bras droit")
                        .position(MANCHE_A_AIR, INerellConstantesServos.POS_BRAS_DROIT_MANCHE_AIR)
                        .position(PHARE, INerellConstantesServos.POS_BRAS_DROIT_PHARE)
                        .position(FERME, INerellConstantesServos.POS_BRAS_DROIT_FERME)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.BRAS_GAUCHE)
                        .setName("Bras gauche")
                        .position(MANCHE_A_AIR, INerellConstantesServos.POS_BRAS_GAUCHE_MANCHE_AIR)
                        .position(PHARE, INerellConstantesServos.POS_BRAS_GAUCHE_PHARE)
                        .position(FERME, INerellConstantesServos.POS_BRAS_GAUCHE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.PAVILLON)
                        .setName("Pavillon")
                        .position(HAUT, INerellConstantesServos.POS_PAVILLON_HAUT)
                        .position(BAS, INerellConstantesServos.POS_PAVILLON_BAS)
                        .position("FIN", INerellConstantesServos.POS_PAVILLON_FIN_MATCH)
                )
        );

        servoConfigs.add(new ServoGroup()
                .setId(INerellConstantesServos.BATCH_MOUSTACHES)
                .setName("Moustaches")
                .batch("Tout ouvert", INerellConstantesServos.POS_BATCH_MOUSTACHES_OUVERT)
                .batch("Tout ouvert spé", INerellConstantesServos.POS_BATCH_MOUSTACHES_OUVERT_SPECIAL)
                .batch("Tout poussette", INerellConstantesServos.POS_BATCH_MOUSTACHES_POUSETTE)
                .batch("Tous fermé haut fond", INerellConstantesServos.POS_BATCH_MOUSTACHES_FERME_HAUT_FOND)
                .batch("Tout fermé", INerellConstantesServos.POS_BATCH_MOUSTACHES_FERME)
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.MOUSTACHE_DROITE)
                        .setName("Moustache droite")
                        .position(OUVERT, INerellConstantesServos.POS_MOUSTACHE_DROITE_OUVERT)
                        .position(OUVERT_SPECIAL, INerellConstantesServos.POS_MOUSTACHE_DROITE_OUVERT_SPECIAL)
                        .position(POUSSETTE, INerellConstantesServos.POS_MOUSTACHE_DROITE_POUSSETTE)
                        .position(FERME, INerellConstantesServos.POS_MOUSTACHE_DROITE_FERME)
                        .position(FERME_HAUT_FOND, INerellConstantesServos.POS_MOUSTACHE_DROITE_FERME_HAUT_FOND)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.MOUSTACHE_GAUCHE)
                        .setName("Moustache gauche")
                        .position(OUVERT, INerellConstantesServos.POS_MOUSTACHE_GAUCHE_OUVERT)
                        .position(OUVERT_SPECIAL, INerellConstantesServos.POS_MOUSTACHE_GAUCHE_OUVERT_SPECIAL)
                        .position(POUSSETTE, INerellConstantesServos.POS_MOUSTACHE_GAUCHE_POUSSETTE)
                        .position(FERME, INerellConstantesServos.POS_MOUSTACHE_GAUCHE_FERME)
                        .position(FERME_HAUT_FOND, INerellConstantesServos.POS_MOUSTACHE_GAUCHE_FERME_HAUT_FOND)
                )
        );

        servoConfigs.add(new ServoGroup()
                .setId(INerellConstantesServos.BATCH_PINCES_ARRIERE)
                .setName("Pinces arrières")
                .batch("Tout ouvert", INerellConstantesServos.POS_BATCH_PINCES_ARRIERE_OUVERT)
                .batch("Tout fermé", INerellConstantesServos.POS_BATCH_PINCES_ARRIERE_FERME)
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.ASCENSEUR_ARRIERE)
                        .setName("Ascenseur arrière")
                        .position("Haut", INerellConstantesServos.POS_ASCENSEUR_ARRIERE_HAUT)
                        .position("Ecueil", INerellConstantesServos.POS_ASCENSEUR_ARRIERE_ECUEIL)
                        .position("Table", INerellConstantesServos.POS_ASCENSEUR_ARRIERE_TABLE)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.PIVOT_ARRIERE)
                        .setName("Pivot arrière")
                        .position(OUVERT, INerellConstantesServos.POS_PIVOT_ARRIERE_OUVERT)
                        .position(FERME, INerellConstantesServos.POS_PIVOT_ARRIERE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.PINCE_ARRIERE_1)
                        .setName("Pince arrière 1")
                        .position(OUVERT, INerellConstantesServos.POS_PINCE_ARRIERE_1_OUVERT)
                        .position(FERME, INerellConstantesServos.POS_PINCE_ARRIERE_1_FERME)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.PINCE_ARRIERE_2)
                        .setName("Pince arrière 2")
                        .position(OUVERT, INerellConstantesServos.POS_PINCE_ARRIERE_2_OUVERT)
                        .position(FERME, INerellConstantesServos.POS_PINCE_ARRIERE_2_FERME)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.PINCE_ARRIERE_3)
                        .setName("Pince arrière 3")
                        .position(OUVERT, INerellConstantesServos.POS_PINCE_ARRIERE_3_OUVERT)
                        .position(FERME, INerellConstantesServos.POS_PINCE_ARRIERE_3_FERME)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.PINCE_ARRIERE_4)
                        .setName("Pince arrière 4")
                        .position(OUVERT, INerellConstantesServos.POS_PINCE_ARRIERE_4_OUVERT)
                        .position(FERME, INerellConstantesServos.POS_PINCE_ARRIERE_4_FERME)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.PINCE_ARRIERE_5)
                        .setName("Pince arrière 5")
                        .position(OUVERT, INerellConstantesServos.POS_PINCE_ARRIERE_5_OUVERT)
                        .position(FERME, INerellConstantesServos.POS_PINCE_ARRIERE_5_FERME)
                )
        );

        servoConfigs.add(new ServoGroup()
                .setId(INerellConstantesServos.BATCH_ASCENSEURS_AVANT)
                .setName("Ascenseurs avants")
                .batch("Tout bas", INerellConstantesServos.POS_BATCH_ASCENSEURS_AVANT_BAS)
                .batch("Tout haut", INerellConstantesServos.POS_BATCH_ASCENSEURS_AVANT_HAUT)
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.ASCENSEUR_AVANT_1)
                        .setName("Ascenseur avant 1")
                        .position(BAS, INerellConstantesServos.POS_ASCENSEUR_AVANT_1_BAS)
                        .position(HAUT, INerellConstantesServos.POS_ASCENSEUR_AVANT_1_HAUT)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.ASCENSEUR_AVANT_2)
                        .setName("Ascenseur avant 2")
                        .position(BAS, INerellConstantesServos.POS_ASCENSEUR_AVANT_2_BAS)
                        .position(HAUT, INerellConstantesServos.POS_ASCENSEUR_AVANT_2_HAUT)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.ASCENSEUR_AVANT_3)
                        .setName("Ascenseur avant 3")
                        .position(BAS, INerellConstantesServos.POS_ASCENSEUR_AVANT_3_BAS)
                        .position(HAUT, INerellConstantesServos.POS_ASCENSEUR_AVANT_3_HAUT)
                )
                .servo(new ServoConfig()
                        .setId(INerellConstantesServos.ASCENSEUR_AVANT_4)
                        .setName("Ascenseur avant 4")
                        .position(BAS, INerellConstantesServos.POS_ASCENSEUR_AVANT_4_BAS)
                        .position(HAUT, INerellConstantesServos.POS_ASCENSEUR_AVANT_4_HAUT)
                )
        );
    }

    @Override
    protected int[][] getBatchPositions(Byte idBatch, Byte position) {
        try {
            return INerellConstantesServos.BATCH_CONFIG.get(idBatch).get(position);
        } catch (Exception e) {
            return new int[0][];
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
