package org.arig.robot.constants;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public interface IConstantesServosNerell {

    // Constantes de vitesse //
    // --------------------- //

    byte SPEED_MOUSTACHE_POUSSETTE = 20;
    byte SPEED_MOUSTACHE = 0;
    byte SPEED_BRAS = 0;
    byte SPEED_ASCENSEUR_AVANT = 0;
    byte SPEED_PINCE_AVANT = 0;
    byte SPEED_ASCENSEUR_ARRIERE = 0;
    byte SPEED_PIVOT_ARRIERE = 0;
    byte SPEED_PINCE_ARRIERE = 0;

    // Tempo servos //
    // ------------ //

    int WAIT_MOUSTACHE_POUSSETTE = 1000;
    int WAIT_MOUSTACHE_DROITE = 550;
    int WAIT_MOUSTACHE_GAUCHE = WAIT_MOUSTACHE_DROITE;
    int WAIT_BRAS_DROIT = 440;
    int WAIT_BRAS_GAUCHE = WAIT_BRAS_DROIT;
    int WAIT_ASCENSEUR_AVANT = 380;
    int WAIT_PINCE_AVANT = 300;
    int WAIT_ASCENSEUR_ARRIERE = 620;
    int WAIT_PIVOT_ARRIERE = 350;
    int WAIT_PINCE_ARRIERE = 400;

    // Constantes d'identification Servo //
    // --------------------------------- //
    byte MOUSTACHE_GAUCHE = 13;
    byte MOUSTACHE_DROITE = 15;
    byte BRAS_GAUCHE = 9;
    byte BRAS_DROIT = 2;
    byte PINCE_AVANT_1 = 12;
    byte PINCE_AVANT_2 = 8;
    byte PINCE_AVANT_3 = 14;
    byte PINCE_AVANT_4 = 7;
    byte PINCE_ARRIERE_1 = 17;
    byte PIVOT_ARRIERE = 16;
    byte PINCE_ARRIERE_2 = 20;
    byte PINCE_ARRIERE_3 = 19;
    byte PINCE_ARRIERE_4 = 18;
    byte PINCE_ARRIERE_5 = 21;
    byte ASCENSEUR_AVANT = 1;
    byte ASCENSEUR_ARRIERE = 10;

    // Constantes de position //
    // ---------------------- //

    int POS_MOUSTACHE_GAUCHE_OUVERT = 1980;
    int POS_MOUSTACHE_GAUCHE_POUSSETTE = 1320;
    int POS_MOUSTACHE_GAUCHE_FERME = 730;
    int POS_MOUSTACHE_DROITE_OUVERT = 990;
    int POS_MOUSTACHE_DROITE_POUSSETTE = 1510;
    int POS_MOUSTACHE_DROITE_FERME = 2180;
    int POS_BRAS_GAUCHE_MANCHE_AIR = 1050;
    int POS_BRAS_GAUCHE_PHARE = 960;
    int POS_BRAS_GAUCHE_FERME = 1980;
    int POS_BRAS_DROIT_MANCHE_AIR = 2110;
    int POS_BRAS_DROIT_PHARE = 2210;
    int POS_BRAS_DROIT_FERME = 1150;
    int POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE = 1880;
    int POS_ASCENSEUR_AVANT_ROULAGE = 1500;
    int POS_ASCENSEUR_AVANT_BAS = 1280;
    int POS_PINCE_AVANT_1_OUVERT = 1770;
    int POS_PINCE_AVANT_1_PRISE = 1540;
    int POS_PINCE_AVANT_1_FERME = 1100;
    int POS_PINCE_AVANT_2_OUVERT = 1300;
    int POS_PINCE_AVANT_2_PRISE = 1560;
    int POS_PINCE_AVANT_2_FERME = 1880;
    int POS_PINCE_AVANT_3_OUVERT = 1690;
    int POS_PINCE_AVANT_3_PRISE = 1480;
    int POS_PINCE_AVANT_3_FERME = 1140;
    int POS_PINCE_AVANT_4_OUVERT = 1340;
    int POS_PINCE_AVANT_4_PRISE = 1590;
    int POS_PINCE_AVANT_4_FERME = 2040;
    int POS_ASCENSEUR_ARRIERE_HAUT = 640;
    int POS_ASCENSEUR_ARRIERE_TABLE = 2240;
    int POS_ASCENSEUR_ARRIERE_ECUEIL = 1920;
    int POS_PIVOT_ARRIERE_OUVERT = 2160;
    int POS_PIVOT_ARRIERE_FERME = 1140;
    int POS_PINCE_ARRIERE_1_OUVERT = 1230;
    int POS_PINCE_ARRIERE_1_FERME = 2050;
    int POS_PINCE_ARRIERE_2_OUVERT = 1500;
    int POS_PINCE_ARRIERE_2_FERME = 690;
    int POS_PINCE_ARRIERE_3_OUVERT = 1500;
    int POS_PINCE_ARRIERE_3_FERME = 750;
    int POS_PINCE_ARRIERE_4_OUVERT = 1400;
    int POS_PINCE_ARRIERE_4_FERME = 2200;
    int POS_PINCE_ARRIERE_5_OUVERT = 1380;
    int POS_PINCE_ARRIERE_5_FERME = 580;

    // Constantes de groupes //
    // --------------------- //

    byte BATCH_PINCES_AVANT = 1;
    byte BATCH_PINCES_ARRIERE = 2;
    byte BATCH_MOUSTACHES = 3;

    byte POS_BATCH_PINCES_AVANT_FERME = 1;
    byte POS_BATCH_PINCES_AVANT_PRISE = 2;
    byte POS_BATCH_PINCES_AVANT_OUVERT = 3;
    byte POS_BATCH_PINCES_ARRIERE_FERME = 4;
    byte POS_BATCH_PINCES_ARRIERE_OUVERT = 5;
    byte POS_BATCH_MOUSTACHES_FERME = 6;
    byte POS_BATCH_MOUSTACHES_OUVERT = 7;
    byte POS_BATCH_MOUSTACHES_POUSETTE = 8;

    Map<Byte, Triple<Integer, Integer, Integer>> MIN_TIME_MAX = ImmutableMap.<Byte, Triple<Integer, Integer, Integer>>builder()
            .put(MOUSTACHE_DROITE, Triple.of(POS_MOUSTACHE_DROITE_FERME, WAIT_MOUSTACHE_DROITE, POS_MOUSTACHE_DROITE_OUVERT))
            .put(MOUSTACHE_GAUCHE, Triple.of(POS_MOUSTACHE_GAUCHE_FERME, WAIT_MOUSTACHE_GAUCHE, POS_MOUSTACHE_GAUCHE_OUVERT))
            .put(BRAS_DROIT, Triple.of(POS_BRAS_DROIT_FERME, WAIT_BRAS_DROIT, POS_BRAS_DROIT_PHARE))
            .put(BRAS_GAUCHE, Triple.of(POS_BRAS_GAUCHE_FERME, WAIT_BRAS_GAUCHE, POS_BRAS_GAUCHE_PHARE))
            .put(ASCENSEUR_AVANT, Triple.of(POS_ASCENSEUR_AVANT_BAS, WAIT_ASCENSEUR_AVANT, POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE))
            .put(PINCE_AVANT_1, Triple.of(POS_PINCE_AVANT_1_FERME, WAIT_PINCE_AVANT, POS_PINCE_AVANT_1_OUVERT))
            .put(PINCE_AVANT_2, Triple.of(POS_PINCE_AVANT_2_FERME, WAIT_PINCE_AVANT, POS_PINCE_AVANT_2_OUVERT))
            .put(PINCE_AVANT_3, Triple.of(POS_PINCE_AVANT_3_FERME, WAIT_PINCE_AVANT, POS_PINCE_AVANT_3_OUVERT))
            .put(PINCE_AVANT_4, Triple.of(POS_PINCE_AVANT_4_FERME, WAIT_PINCE_AVANT, POS_PINCE_AVANT_4_OUVERT))
            .put(ASCENSEUR_ARRIERE, Triple.of(POS_ASCENSEUR_ARRIERE_TABLE, WAIT_ASCENSEUR_ARRIERE, POS_ASCENSEUR_ARRIERE_HAUT))
            .put(PIVOT_ARRIERE, Triple.of(POS_PIVOT_ARRIERE_FERME, WAIT_PIVOT_ARRIERE, POS_PIVOT_ARRIERE_OUVERT))
            .put(PINCE_ARRIERE_1, Triple.of(POS_PINCE_ARRIERE_1_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_1_OUVERT))
            .put(PINCE_ARRIERE_2, Triple.of(POS_PINCE_ARRIERE_2_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_2_OUVERT))
            .put(PINCE_ARRIERE_3, Triple.of(POS_PINCE_ARRIERE_3_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_3_OUVERT))
            .put(PINCE_ARRIERE_4, Triple.of(POS_PINCE_ARRIERE_4_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_4_OUVERT))
            .put(PINCE_ARRIERE_5, Triple.of(POS_PINCE_ARRIERE_5_FERME, WAIT_PINCE_ARRIERE, POS_PINCE_ARRIERE_5_OUVERT))
            .build();

    Map<Byte, Map<Byte, int[][]>> BATCH_CONFIG = ImmutableMap.<Byte, Map<Byte, int[][]>>builder()
            .put(BATCH_PINCES_ARRIERE, ImmutableMap.<Byte, int[][]>builder()
                    .put(POS_BATCH_PINCES_ARRIERE_FERME, new int[][]{
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_1, IConstantesServosNerell.POS_PINCE_ARRIERE_1_FERME},
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_2, IConstantesServosNerell.POS_PINCE_ARRIERE_2_FERME},
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_3, IConstantesServosNerell.POS_PINCE_ARRIERE_3_FERME},
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_4, IConstantesServosNerell.POS_PINCE_ARRIERE_4_FERME},
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_5, IConstantesServosNerell.POS_PINCE_ARRIERE_5_FERME}
                    })
                    .put(POS_BATCH_PINCES_ARRIERE_OUVERT, new int[][]{
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_1, IConstantesServosNerell.POS_PINCE_ARRIERE_1_OUVERT},
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_2, IConstantesServosNerell.POS_PINCE_ARRIERE_2_OUVERT},
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_3, IConstantesServosNerell.POS_PINCE_ARRIERE_3_OUVERT},
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_4, IConstantesServosNerell.POS_PINCE_ARRIERE_4_OUVERT},
                            new int[]{IConstantesServosNerell.PINCE_ARRIERE_5, IConstantesServosNerell.POS_PINCE_ARRIERE_5_OUVERT}
                    })
                    .build())
            .put(BATCH_PINCES_AVANT, ImmutableMap.<Byte, int[][]>builder()
                    .put(POS_BATCH_PINCES_AVANT_FERME, new int[][]{
                            new int[]{IConstantesServosNerell.PINCE_AVANT_1, IConstantesServosNerell.POS_PINCE_AVANT_1_FERME},
                            new int[]{IConstantesServosNerell.PINCE_AVANT_2, IConstantesServosNerell.POS_PINCE_AVANT_2_FERME},
                            new int[]{IConstantesServosNerell.PINCE_AVANT_3, IConstantesServosNerell.POS_PINCE_AVANT_3_FERME},
                            new int[]{IConstantesServosNerell.PINCE_AVANT_4, IConstantesServosNerell.POS_PINCE_AVANT_4_FERME}
                    })
                    .put(POS_BATCH_PINCES_AVANT_PRISE, new int[][]{
                            new int[]{IConstantesServosNerell.PINCE_AVANT_1, IConstantesServosNerell.POS_PINCE_AVANT_1_PRISE},
                            new int[]{IConstantesServosNerell.PINCE_AVANT_2, IConstantesServosNerell.POS_PINCE_AVANT_2_PRISE},
                            new int[]{IConstantesServosNerell.PINCE_AVANT_3, IConstantesServosNerell.POS_PINCE_AVANT_3_PRISE},
                            new int[]{IConstantesServosNerell.PINCE_AVANT_4, IConstantesServosNerell.POS_PINCE_AVANT_4_PRISE}
                    })
                    .put(POS_BATCH_PINCES_AVANT_OUVERT, new int[][]{
                            new int[]{IConstantesServosNerell.PINCE_AVANT_1, IConstantesServosNerell.POS_PINCE_AVANT_1_OUVERT},
                            new int[]{IConstantesServosNerell.PINCE_AVANT_2, IConstantesServosNerell.POS_PINCE_AVANT_2_OUVERT},
                            new int[]{IConstantesServosNerell.PINCE_AVANT_3, IConstantesServosNerell.POS_PINCE_AVANT_3_OUVERT},
                            new int[]{IConstantesServosNerell.PINCE_AVANT_4, IConstantesServosNerell.POS_PINCE_AVANT_4_OUVERT}
                    })
                    .build())
            .put(BATCH_MOUSTACHES, ImmutableMap.<Byte, int[][]>builder()
                    .put(POS_BATCH_MOUSTACHES_FERME, new int[][]{
                            new int[]{IConstantesServosNerell.MOUSTACHE_DROITE, IConstantesServosNerell.POS_MOUSTACHE_DROITE_FERME},
                            new int[]{IConstantesServosNerell.MOUSTACHE_GAUCHE, IConstantesServosNerell.POS_MOUSTACHE_GAUCHE_FERME}
                    })
                    .put(POS_BATCH_MOUSTACHES_OUVERT, new int[][]{
                            new int[]{IConstantesServosNerell.MOUSTACHE_DROITE, IConstantesServosNerell.POS_MOUSTACHE_DROITE_OUVERT},
                            new int[]{IConstantesServosNerell.MOUSTACHE_GAUCHE, IConstantesServosNerell.POS_MOUSTACHE_GAUCHE_OUVERT}
                    })
                    .put(POS_BATCH_MOUSTACHES_POUSETTE, new int[][]{
                            new int[]{IConstantesServosNerell.MOUSTACHE_DROITE, IConstantesServosNerell.POS_MOUSTACHE_DROITE_POUSSETTE},
                            new int[]{IConstantesServosNerell.MOUSTACHE_GAUCHE, IConstantesServosNerell.POS_MOUSTACHE_GAUCHE_POUSSETTE}
                    })
                    .build())
            .build();
}
