package org.arig.robot.constants;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public interface IConstantesServosOdin {

    double SEUIL_ALIMENTATION_VOLTS = 3;

    // Constantes de vitesse //
    // --------------------- //

    byte SPEED_BRAS = 0;
    byte SPEED_PAVILLON = 0;
    byte SPEED_POUSSOIR = 0;
    byte SPEED_POUSSOIR_POUSSE = 20; // TODO

    // Tempo servos //
    // ------------ //

    int WAIT_BRAS = 440;
    int WAIT_PAVILLON = 300;
    int WAIT_POUSSOIR = 500; // TODO

    // Constantes d'identification Servo //
    // --------------------------------- //
    byte BRAS_GAUCHE = 1; // TODO
    byte BRAS_DROIT = 2; // TODO
    byte PAVILLON = 3; // TODO
    byte POUSSOIR_AVANT_GAUCHE = 4; // TODO
    byte POUSSOIR_AVANT_DROIT = 5; // TODO
    byte POUSSOIR_ARRIERE_GAUCHE = 6; // TODO
    byte POUSSOIR_ARRIERE_DROIT = 7; // TODO

    // Constantes de position //
    // ---------------------- //

    int POS_BRAS_GAUCHE_MANCHE_AIR = 1500; // TODO
    int POS_BRAS_GAUCHE_PHARE = 1500; // TODO
    int POS_BRAS_GAUCHE_FERME = 1500; // TODO
    int POS_BRAS_DROIT_MANCHE_AIR = 1500; // TODO
    int POS_BRAS_DROIT_PHARE = 1500; // TODO
    int POS_BRAS_DROIT_FERME = 1500; // TODO
    int POS_PAVILLON_HAUT = 1500; // TODO
    int POS_PAVILLON_FIN_MATCH = 1500; // TODO
    int POS_PAVILLON_BAS = 1500; // TODO
    int POS_POUSSOIR_AVANT_GAUCHE_BAS = 1500; // TODO
    int POS_POUSSOIR_AVANT_GAUCHE_HAUT = 1500; // TODO
    int POS_POUSSOIR_AVANT_DROIT_BAS = 1500; // TODO
    int POS_POUSSOIR_AVANT_DROIT_HAUT = 1500; // TODO
    int POS_POUSSOIR_ARRIERE_GAUCHE_BAS = 1500; // TODO
    int POS_POUSSOIR_ARRIERE_GAUCHE_HAUT = 1500; // TODO
    int POS_POUSSOIR_ARRIERE_DROIT_BAS = 1500; // TODO
    int POS_POUSSOIR_ARRIERE_DROIT_HAUT = 1500; // TODO

    // Constantes de groupes //
    // --------------------- //

    byte BATCH_POUSSOIR_AVANT = 1;
    byte BATCH_POUSSOIR_ARRIERE = 2;

    byte POS_BATCH_POUSSOIR_AVANT_BAS = 1;
    byte POS_BATCH_POUSSOIR_AVANT_HAUT = 2;
    byte POS_BATCH_POUSSOIR_ARRIERE_BAS = 3;
    byte POS_BATCH_POUSSOIR_ARRIERE_HAUT = 4;

    Map<Byte, Triple<Integer, Integer, Integer>> MIN_TIME_MAX = ImmutableMap.<Byte, Triple<Integer, Integer, Integer>>builder()
            .put(BRAS_DROIT, Triple.of(POS_BRAS_DROIT_FERME, WAIT_BRAS, POS_BRAS_DROIT_PHARE))
            .put(BRAS_GAUCHE, Triple.of(POS_BRAS_GAUCHE_FERME, WAIT_BRAS, POS_BRAS_GAUCHE_PHARE))
            .put(PAVILLON, Triple.of(POS_PAVILLON_BAS, WAIT_PAVILLON, POS_PAVILLON_HAUT))
            .put(POUSSOIR_AVANT_GAUCHE, Triple.of(POS_POUSSOIR_AVANT_GAUCHE_BAS, WAIT_POUSSOIR, POS_POUSSOIR_AVANT_GAUCHE_HAUT))
            .put(POUSSOIR_AVANT_DROIT, Triple.of(POS_POUSSOIR_AVANT_DROIT_BAS, WAIT_POUSSOIR, POS_POUSSOIR_AVANT_DROIT_HAUT))
            .put(POUSSOIR_ARRIERE_GAUCHE, Triple.of(POS_POUSSOIR_ARRIERE_GAUCHE_BAS, WAIT_POUSSOIR, POS_POUSSOIR_ARRIERE_GAUCHE_HAUT))
            .put(POUSSOIR_ARRIERE_DROIT, Triple.of(POS_POUSSOIR_ARRIERE_DROIT_BAS, WAIT_POUSSOIR, POS_POUSSOIR_ARRIERE_DROIT_HAUT))
            .build();

    Map<Byte, Map<Byte, int[][]>> BATCH_CONFIG = ImmutableMap.<Byte, Map<Byte, int[][]>>builder()
            .put(BATCH_POUSSOIR_AVANT, ImmutableMap.<Byte, int[][]>builder()
                    .put(POS_BATCH_POUSSOIR_AVANT_BAS, new int[][]{
                            new int[]{IConstantesServosOdin.POUSSOIR_AVANT_GAUCHE, IConstantesServosOdin.POS_POUSSOIR_AVANT_GAUCHE_BAS},
                            new int[]{IConstantesServosOdin.POUSSOIR_AVANT_DROIT, IConstantesServosOdin.POS_POUSSOIR_AVANT_DROIT_BAS}
                    })
                    .put(POS_BATCH_POUSSOIR_AVANT_HAUT, new int[][]{
                            new int[]{IConstantesServosOdin.POUSSOIR_AVANT_GAUCHE, IConstantesServosOdin.POS_POUSSOIR_AVANT_GAUCHE_HAUT},
                            new int[]{IConstantesServosOdin.POUSSOIR_AVANT_DROIT, IConstantesServosOdin.POS_POUSSOIR_AVANT_DROIT_HAUT}
                    })
                    .build())
            .put(BATCH_POUSSOIR_ARRIERE, ImmutableMap.<Byte, int[][]>builder()
                    .put(POS_BATCH_POUSSOIR_ARRIERE_BAS, new int[][]{
                            new int[]{IConstantesServosOdin.POUSSOIR_ARRIERE_GAUCHE, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_GAUCHE_BAS},
                            new int[]{IConstantesServosOdin.POUSSOIR_ARRIERE_DROIT, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_DROIT_BAS}
                    })
                    .put(POS_BATCH_POUSSOIR_ARRIERE_HAUT, new int[][]{
                            new int[]{IConstantesServosOdin.POUSSOIR_ARRIERE_GAUCHE, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_GAUCHE_HAUT},
                            new int[]{IConstantesServosOdin.POUSSOIR_ARRIERE_DROIT, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_DROIT_HAUT}
                    })
                    .build())
            .build();
}
