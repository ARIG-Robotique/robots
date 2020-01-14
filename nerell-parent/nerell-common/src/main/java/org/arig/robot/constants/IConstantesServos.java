package org.arig.robot.constants;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public interface IConstantesServos {

    // Constantes de vitesse //
    // --------------------- //

    byte SPEED_ASCENSEUR_AVANT = 0;

    // Tempo servos //
    // ------------ //

    int WAIT_ASCENSEUR_AVANT = 1500;

    // Constantes d'identification Servo //
    // --------------------------------- //

    byte MOTOR_DROIT = 15;
    byte MOTOR_GAUCHE = 12;

    byte ASCENSEUR_AVANT = 6;

    // Constantes de position //
    // ---------------------- //

    int POS_ASCENSEUR_AVANT_HAUT = 420;
    int POS_ASCENSEUR_AVANT_BAS = 420;

    Map<Byte, Triple<Integer, Integer, Integer>> MIN_TIME_MAX = ImmutableMap.<Byte, Triple<Integer, Integer, Integer>>builder()
            .put(ASCENSEUR_AVANT, Triple.of(POS_ASCENSEUR_AVANT_HAUT, WAIT_ASCENSEUR_AVANT, POS_ASCENSEUR_AVANT_BAS))
            .build();
}
