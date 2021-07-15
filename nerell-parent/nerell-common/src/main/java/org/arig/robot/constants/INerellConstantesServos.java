package org.arig.robot.constants;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public interface INerellConstantesServos {

    // Constantes de vitesse //
    // --------------------- //

    byte SPEED_SERVO1 = 0;

    // Tempo servos //
    // ------------ //

    int WAIT_SERVO1 = 300;

    // Constantes d'identification Servo //
    // --------------------------------- //
    byte SERVO1 = 1;

    // Constantes de position //
    // ---------------------- //

    int POS_SERVO1_OUVERT = 1500;
    int POS_SERVO1_FERME = 1500;

    // Constantes de groupes //
    // --------------------- //

    byte BATCH1 = 1;

    byte POS_BATCH1_FERME = 1;
    byte POS_BATCH1_OUVERT = 2;

    Map<Byte, Triple<Integer, Integer, Integer>> MIN_TIME_MAX = ImmutableMap.<Byte, Triple<Integer, Integer, Integer>>builder()
            .put(SERVO1, Triple.of(POS_SERVO1_FERME, WAIT_SERVO1, POS_SERVO1_OUVERT))
            .build();

    Map<Byte, Map<Byte, int[][]>> BATCH_CONFIG = ImmutableMap.<Byte, Map<Byte, int[][]>>builder()
            .put(BATCH1, ImmutableMap.<Byte, int[][]>builder()
                    .put(POS_BATCH1_FERME, new int[][]{
                            new int[]{INerellConstantesServos.SERVO1, INerellConstantesServos.POS_SERVO1_FERME}
                    })
                    .put(POS_BATCH1_OUVERT, new int[][]{
                            new int[]{INerellConstantesServos.SERVO1, INerellConstantesServos.POS_SERVO1_OUVERT}
                    })
                    .build())
            .build();
}
