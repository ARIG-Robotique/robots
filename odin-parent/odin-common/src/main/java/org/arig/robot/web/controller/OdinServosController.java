package org.arig.robot.web.controller;

import org.arig.robot.constants.IConstantesServosOdin;
import org.arig.robot.model.servos.ServoConfig;
import org.arig.robot.model.servos.ServoGroup;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OdinServosController extends AbstractServosController {

    private static final String OUVERT = "Ouvert";
    private static final String FERME = "Fermé";
    private static final String MANCHE_A_AIR = "Manche à air";
    private static final String PHARE = "Phare";
    private static final String BAS = "Bas";
    private static final String HAUT = "Haut";

    private static List<ServoGroup> servoConfigs = new ArrayList<>();

    static {
        servoConfigs.add(new ServoGroup()
                .setName("Autres")
                .servo(new ServoConfig()
                        .setId(IConstantesServosOdin.BRAS_DROIT)
                        .setName("Bras droit")
                        .position(MANCHE_A_AIR, IConstantesServosOdin.POS_BRAS_DROIT_MANCHE_AIR)
                        .position(PHARE, IConstantesServosOdin.POS_BRAS_DROIT_PHARE)
                        .position(FERME, IConstantesServosOdin.POS_BRAS_DROIT_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosOdin.BRAS_GAUCHE)
                        .setName("Bras gauche")
                        .position(MANCHE_A_AIR, IConstantesServosOdin.POS_BRAS_GAUCHE_MANCHE_AIR)
                        .position(PHARE, IConstantesServosOdin.POS_BRAS_GAUCHE_PHARE)
                        .position(FERME, IConstantesServosOdin.POS_BRAS_GAUCHE_FERME)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosOdin.PAVILLON)
                        .setName("Pavillon")
                        .position(HAUT, IConstantesServosOdin.POS_PAVILLON_HAUT)
                        .position(BAS, IConstantesServosOdin.POS_PAVILLON_BAS)
                )
        );

        servoConfigs.add(new ServoGroup()
                .setName("Poussoirs avant")
                .setId(IConstantesServosOdin.BATCH_POUSSOIR_AVANT)
                .batch("Tout haut", IConstantesServosOdin.POS_BATCH_POUSSOIR_AVANT_HAUT)
                .batch("Tout bas", IConstantesServosOdin.POS_BATCH_POUSSOIR_AVANT_BAS)
                .servo(new ServoConfig()
                        .setId(IConstantesServosOdin.POUSSOIR_AVANT_GAUCHE)
                        .setName("Avant gauche")
                        .position(BAS, IConstantesServosOdin.POS_POUSSOIR_AVANT_GAUCHE_BAS)
                        .position(HAUT, IConstantesServosOdin.POS_POUSSOIR_AVANT_GAUCHE_HAUT)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosOdin.POUSSOIR_AVANT_DROIT)
                        .setName("Avant droit")
                        .position(BAS, IConstantesServosOdin.POS_POUSSOIR_AVANT_DROIT_BAS)
                        .position(HAUT, IConstantesServosOdin.POS_POUSSOIR_AVANT_DROIT_HAUT)
                )
        );

        servoConfigs.add(new ServoGroup()
                .setName("Poussoirs arriere")
                .setId(IConstantesServosOdin.BATCH_POUSSOIR_ARRIERE)
                .batch("Tout haut", IConstantesServosOdin.POS_BATCH_POUSSOIR_ARRIERE_HAUT)
                .batch("Tout bas", IConstantesServosOdin.POS_BATCH_POUSSOIR_ARRIERE_BAS)
                .servo(new ServoConfig()
                        .setId(IConstantesServosOdin.POUSSOIR_ARRIERE_GAUCHE)
                        .setName("Arriere gauche")
                        .position(BAS, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_GAUCHE_BAS)
                        .position(HAUT, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_GAUCHE_HAUT)
                )
                .servo(new ServoConfig()
                        .setId(IConstantesServosOdin.POUSSOIR_ARRIERE_DROIT)
                        .setName("Arriere droit")
                        .position(BAS, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_DROIT_BAS)
                        .position(HAUT, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_DROIT_HAUT)
                )
        );
    }

    @Override
    protected int[][] getBatchPositions(final Byte idBatch, final Byte position) {
        try {
            return IConstantesServosOdin.BATCH_CONFIG.get(idBatch).get(position);
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
