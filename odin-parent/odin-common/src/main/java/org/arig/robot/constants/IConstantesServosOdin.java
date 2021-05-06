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

    // Tempo servos //
    // ------------ //

    int WAIT_BRAS_DROIT = 440;
    int WAIT_BRAS_GAUCHE = WAIT_BRAS_DROIT;
    int WAIT_PAVILLON = 300;

    // Constantes d'identification Servo //
    // --------------------------------- //
    byte BRAS_GAUCHE = 1; // TODO
    byte BRAS_DROIT = 2; // TODO
    byte PAVILLON = 3; // TODO

    // Constantes de position //
    // ---------------------- //

    int POS_BRAS_GAUCHE_MANCHE_AIR = 1500; // TODO
    int POS_BRAS_GAUCHE_PHARE = 1500; // TODO
    int POS_BRAS_GAUCHE_FERME = 1500; // TODO
    int POS_BRAS_DROIT_MANCHE_AIR = 1500; // TODO
    int POS_BRAS_DROIT_PHARE = 1500; // TODO
    int POS_BRAS_DROIT_FERME = 1500; // TODO
    int POS_PAVILLON_HAUT = 1500; // TODO
    int POS_PAVILLON_BAS = 1500; // TODO

    // Constantes de groupes //
    // --------------------- //

    Map<Byte, Triple<Integer, Integer, Integer>> MIN_TIME_MAX = ImmutableMap.<Byte, Triple<Integer, Integer, Integer>>builder()
            .put(BRAS_DROIT, Triple.of(POS_BRAS_DROIT_FERME, WAIT_BRAS_DROIT, POS_BRAS_DROIT_PHARE))
            .put(BRAS_GAUCHE, Triple.of(POS_BRAS_GAUCHE_FERME, WAIT_BRAS_GAUCHE, POS_BRAS_GAUCHE_PHARE))
            .put(PAVILLON, Triple.of(POS_PAVILLON_BAS, WAIT_PAVILLON, POS_PAVILLON_HAUT))
            .build();
}
