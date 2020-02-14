package org.arig.robot.constants;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public interface IConstantesServos {

    // Constantes de vitesse //
    // --------------------- //

    byte SPEED_MOUSTACHE_DROITE = 0;
    byte SPEED_MOUSTACHE_GAUCHE = 0;
    byte SPEED_POUSSOIR_DROITE = 0;
    byte SPEED_POUSSOIR_GAUCHE = 0;
    byte SPEED_ASCENSEUR_AVANT = 0;
    byte SPEED_PINCE_AVANT = 0;
    byte SPEED_ASCENSEUR_ARRIERE = 0;
    byte SPEED_PIVOT_ARRIERE = 0;
    byte SPEED_PINCE_ARRIERE = 0;

    // Tempo servos //
    // ------------ //

    int WAIT_MOUSTACHE_DROITE = 1000;
    int WAIT_MOUSTACHE_GAUCHE = 1000;
    int WAIT_POUSSOIR_DROITE = 1000;
    int WAIT_POUSSOIR_GAUCHE = 1000;
    int WAIT_ASCENSEUR_AVANT = 1500;
    int WAIT_PINCE_AVANT = 500;
    int WAIT_ASCENSEUR_ARRIERE = 1500;
    int WAIT_PIVOT_ARRIERE = 1000;
    int WAIT_PINCE_ARRIERE = 500;

    // Constantes d'identification Servo //
    // --------------------------------- //

    byte MOTOR_DROIT = 15;
    byte MOTOR_GAUCHE = 12;

    byte MOUSTACHE_DROITE = 1;
    byte MOUSTACHE_GAUCHE = 2;
    byte POUSSOIR_DROITE = 3;
    byte POUSSOIR_GAUCHE = 4;
    byte ASCENSEUR_AVANT = 5;
    byte PINCE_AVANT_1 = 6;
    byte PINCE_AVANT_2 = 7;
    byte PINCE_AVANT_3 = 8;
    byte PINCE_AVANT_4 = 9;
    byte ASCENSEUR_ARRIERE = 10;
    byte PIVOT_ARRIERE = 11;
    byte PINCE_ARRIERE_1 = 13;
    byte PINCE_ARRIERE_2 = 14;
    byte PINCE_ARRIERE_3 = 16;
    byte PINCE_ARRIERE_4 = 17;
    byte PINCE_ARRIERE_5 = 18;

    // Constantes de position //
    // ---------------------- //

    int POS_MOUSTACHE_DROITE_OUVERT = 1500;
    int POS_MOUSTACHE_DROITE_FERME = 1500;
    int POS_MOUSTACHE_GAUCHE_OUVERT = 1500;
    int POS_MOUSTACHE_GAUCHE_FERME = 1500;
    int POS_POUSSOIR_DROITE_OUVERT = 1500;
    int POS_POUSSOIR_DROITE_FERME = 1500;
    int POS_POUSSOIR_GAUCHE_OUVERT = 1500;
    int POS_POUSSOIR_GAUCHE_FERME = 1500;
    int POS_ASCENSEUR_AVANT_HAUT = 1500;
    int POS_ASCENSEUR_AVANT_BAS = 1500;
    int POS_PINCE_AVANT_1_OUVERT = 1500;
    int POS_PINCE_AVANT_1_FERME = 1500;
    int POS_PINCE_AVANT_2_OUVERT = 1500;
    int POS_PINCE_AVANT_2_FERME = 1500;
    int POS_PINCE_AVANT_3_OUVERT = 1500;
    int POS_PINCE_AVANT_3_FERME = 1500;
    int POS_PINCE_AVANT_4_OUVERT = 1500;
    int POS_PINCE_AVANT_4_FERME = 1500;
    int POS_ASCENSEUR_ARRIERE_HAUT = 1500;
    int POS_ASCENSEUR_ARRIERE_BAS = 1500;
    int POS_PIVOT_ARRIERE_OUVERT = 1500;
    int POS_PIVOT_ARRIERE_FERME = 1500;
    int POS_PINCE_ARRIERE_1_OUVERT = 1500;
    int POS_PINCE_ARRIERE_1_FERME = 1500;
    int POS_PINCE_ARRIERE_2_OUVERT = 1500;
    int POS_PINCE_ARRIERE_2_FERME = 1500;
    int POS_PINCE_ARRIERE_3_OUVERT = 1500;
    int POS_PINCE_ARRIERE_3_FERME = 1500;
    int POS_PINCE_ARRIERE_4_OUVERT = 1500;
    int POS_PINCE_ARRIERE_4_FERME = 1500;
    int POS_PINCE_ARRIERE_5_OUVERT = 1500;
    int POS_PINCE_ARRIERE_5_FERME = 1500;

    // Constantes de groupes //
    // --------------------- //

    byte GROUPE_PINCES_AVANT = 1;
    byte GROUPE_PINCES_ARRIERE = 2;
    byte GROUPE_MOUSTACHES = 3;

    byte POS_GROUPE_PINCES_AVANT_FERME = 1;
    byte POS_GROUPE_PINCES_AVANT_OUVERT = 2;
    byte POS_GROUPE_PINCES_ARRIERE_FERME = 3;
    byte POS_GROUPE_PINCES_ARRIERE_OUVERT = 4;
    byte POS_GROUPE_MOUSTACHES_FERME = 5;
    byte POS_GROUPE_MOUSTACHES_OUVERT = 6;

    Map<Byte, Triple<Integer, Integer, Integer>> MIN_TIME_MAX = ImmutableMap.<Byte, Triple<Integer, Integer, Integer>>builder()
            .put(MOUSTACHE_DROITE, Triple.of(POS_MOUSTACHE_DROITE_FERME, WAIT_MOUSTACHE_DROITE, POS_MOUSTACHE_DROITE_OUVERT))
            .put(MOUSTACHE_GAUCHE, Triple.of(POS_MOUSTACHE_GAUCHE_FERME, WAIT_MOUSTACHE_GAUCHE, POS_MOUSTACHE_GAUCHE_OUVERT))
            .put(POUSSOIR_DROITE, Triple.of(POS_POUSSOIR_DROITE_FERME, WAIT_POUSSOIR_DROITE, POS_POUSSOIR_DROITE_OUVERT))
            .put(POUSSOIR_GAUCHE, Triple.of(POS_POUSSOIR_GAUCHE_FERME, WAIT_POUSSOIR_GAUCHE, POS_POUSSOIR_GAUCHE_OUVERT))
            .put(ASCENSEUR_AVANT, Triple.of(POS_ASCENSEUR_AVANT_BAS, WAIT_ASCENSEUR_AVANT, POS_ASCENSEUR_AVANT_HAUT))
            .put(PINCE_AVANT_1, Triple.of(POS_PINCE_AVANT_1_FERME, WAIT_PINCE_AVANT, POS_PINCE_AVANT_1_OUVERT))
            .put(PINCE_AVANT_2, Triple.of(POS_PINCE_AVANT_2_FERME, WAIT_PINCE_AVANT, POS_PINCE_AVANT_2_OUVERT))
            .put(PINCE_AVANT_3, Triple.of(POS_PINCE_AVANT_3_FERME, WAIT_PINCE_AVANT, POS_PINCE_AVANT_3_OUVERT))
            .put(PINCE_AVANT_4, Triple.of(POS_PINCE_AVANT_4_FERME, WAIT_PINCE_AVANT, POS_PINCE_AVANT_4_OUVERT))
            .put(ASCENSEUR_ARRIERE, Triple.of(POS_ASCENSEUR_ARRIERE_BAS, WAIT_ASCENSEUR_ARRIERE, POS_ASCENSEUR_ARRIERE_HAUT))
            .put(PIVOT_ARRIERE, Triple.of(POS_PIVOT_ARRIERE_FERME, WAIT_PIVOT_ARRIERE, POS_PIVOT_ARRIERE_OUVERT))
            .put(PINCE_ARRIERE_1, Triple.of(POS_PINCE_ARRIERE_1_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_1_OUVERT))
            .put(PINCE_ARRIERE_2, Triple.of(POS_PINCE_ARRIERE_2_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_2_OUVERT))
            .put(PINCE_ARRIERE_3, Triple.of(POS_PINCE_ARRIERE_3_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_3_OUVERT))
            .put(PINCE_ARRIERE_4, Triple.of(POS_PINCE_ARRIERE_4_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_4_OUVERT))
            .put(PINCE_ARRIERE_5, Triple.of(POS_PINCE_ARRIERE_5_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_5_OUVERT))
            .build();

    Map<Byte, Map<Byte, int[][]>> GROUP_CONFIG = ImmutableMap.<Byte, Map<Byte, int[][]>>builder()
            .put(GROUPE_PINCES_ARRIERE, ImmutableMap.<Byte, int[][]>builder()
                    .put(POS_GROUPE_PINCES_ARRIERE_FERME, new int[][]{
                            new int[]{IConstantesServos.PINCE_ARRIERE_1, IConstantesServos.POS_PINCE_ARRIERE_1_FERME},
                            new int[]{IConstantesServos.PINCE_ARRIERE_2, IConstantesServos.POS_PINCE_ARRIERE_2_FERME},
                            new int[]{IConstantesServos.PINCE_ARRIERE_3, IConstantesServos.POS_PINCE_ARRIERE_3_FERME},
                            new int[]{IConstantesServos.PINCE_ARRIERE_4, IConstantesServos.POS_PINCE_ARRIERE_4_FERME},
                            new int[]{IConstantesServos.PINCE_ARRIERE_5, IConstantesServos.POS_PINCE_ARRIERE_5_FERME}
                    })
                    .put(POS_GROUPE_PINCES_ARRIERE_OUVERT, new int[][]{
                            new int[]{IConstantesServos.PINCE_ARRIERE_1, IConstantesServos.POS_PINCE_ARRIERE_1_OUVERT},
                            new int[]{IConstantesServos.PINCE_ARRIERE_2, IConstantesServos.POS_PINCE_ARRIERE_2_OUVERT},
                            new int[]{IConstantesServos.PINCE_ARRIERE_3, IConstantesServos.POS_PINCE_ARRIERE_3_OUVERT},
                            new int[]{IConstantesServos.PINCE_ARRIERE_4, IConstantesServos.POS_PINCE_ARRIERE_4_OUVERT},
                            new int[]{IConstantesServos.PINCE_ARRIERE_5, IConstantesServos.POS_PINCE_ARRIERE_5_OUVERT}
                    })
                    .build())
            .put(GROUPE_PINCES_AVANT, ImmutableMap.<Byte, int[][]>builder()
                    .put(POS_GROUPE_PINCES_AVANT_FERME, new int[][]{
                            new int[]{IConstantesServos.PINCE_AVANT_1, IConstantesServos.POS_PINCE_AVANT_1_FERME},
                            new int[]{IConstantesServos.PINCE_AVANT_2, IConstantesServos.POS_PINCE_AVANT_2_FERME},
                            new int[]{IConstantesServos.PINCE_AVANT_3, IConstantesServos.POS_PINCE_AVANT_3_FERME},
                            new int[]{IConstantesServos.PINCE_AVANT_4, IConstantesServos.POS_PINCE_AVANT_4_FERME}
                    })
                    .put(POS_GROUPE_PINCES_AVANT_OUVERT, new int[][]{
                            new int[]{IConstantesServos.PINCE_AVANT_1, IConstantesServos.POS_PINCE_AVANT_1_OUVERT},
                            new int[]{IConstantesServos.PINCE_AVANT_2, IConstantesServos.POS_PINCE_AVANT_2_OUVERT},
                            new int[]{IConstantesServos.PINCE_AVANT_3, IConstantesServos.POS_PINCE_AVANT_3_OUVERT},
                            new int[]{IConstantesServos.PINCE_AVANT_4, IConstantesServos.POS_PINCE_AVANT_4_OUVERT}
                    })
                    .build())
            .put(GROUPE_MOUSTACHES, ImmutableMap.<Byte, int[][]>builder()
                    .put(POS_GROUPE_MOUSTACHES_FERME, new int[][]{
                            new int[]{IConstantesServos.MOUSTACHE_DROITE, IConstantesServos.POS_MOUSTACHE_DROITE_FERME},
                            new int[]{IConstantesServos.MOUSTACHE_GAUCHE, IConstantesServos.POS_MOUSTACHE_GAUCHE_FERME}
                    })
                    .put(POS_GROUPE_MOUSTACHES_OUVERT, new int[][]{
                            new int[]{IConstantesServos.MOUSTACHE_DROITE, IConstantesServos.POS_MOUSTACHE_DROITE_OUVERT},
                            new int[]{IConstantesServos.MOUSTACHE_GAUCHE, IConstantesServos.POS_MOUSTACHE_GAUCHE_OUVERT}
                    })
                    .build())
            .build();
}
