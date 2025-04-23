package org.arig.robot.model.balise.enums;

import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Team;

public enum Data3DName {
    UNKNOWN,
    BOARD,
    BLEU_1,
    BLEU_2,
    BLEU_3,
    BLEU_4,
    BLEU_5,
    JAUNE_6,
    JAUNE_7,
    JAUNE_8,
    JAUNE_9,
    JAUNE_10,
    TRIANGLE,
    CARE,
    ROND,
    SOLAR_PANEL_0,
    SOLAR_PANEL_1,
    SOLAR_PANEL_2,
    SOLAR_PANEL_3,
    SOLAR_PANEL_4,
    SOLAR_PANEL_5,
    SOLAR_PANEL_6,
    SOLAR_PANEL_7,
    SOLAR_PANEL_8,
    FRAGILE,
    RESISTANTE,
    POT,
    PLANTEPOT,
    NORD,
    NORD_OUEST,
    NORD_EST,
    SUD,
    SUD_EST,
    SUD_OUEST,
    JAUNE_MILIEU,
    JAUNE_SUD,
    JAUNE_NORD,
    BLEU_MILIEU,
    BLEU_SUD,
    BLEU_NORD;

    public static Integer getSolarPannelNumber(Data3DName data3DName) {
        return switch (data3DName) {
            case SOLAR_PANEL_0 -> 1;
            case SOLAR_PANEL_1 -> 2;
            case SOLAR_PANEL_2 -> 3;
            case SOLAR_PANEL_3 -> 4;
            case SOLAR_PANEL_4 -> 5;
            case SOLAR_PANEL_5 -> 6;
            case SOLAR_PANEL_6 -> 7;
            case SOLAR_PANEL_7 -> 8;
            case SOLAR_PANEL_8 -> 9;
            default -> null;
        };
    }

    public static Team getRobotTeam(Data3DName data3DName) {
        return switch (data3DName) {
            case BLEU_1, BLEU_2, BLEU_3, BLEU_4, BLEU_5 -> Team.BLEU;
            case JAUNE_6, JAUNE_7, JAUNE_8, JAUNE_9, JAUNE_10 -> Team.JAUNE;
            default -> null;
        };
    }

    public static GradinBrut.ID getStockPlantesID(Data3DName data3DName) {
        return switch (data3DName) {
            // TODO : Add vision name !
            /*
            case NORD -> Plante.ID.STOCK_NORD;
            case NORD_OUEST -> Plante.ID.STOCK_NORD_OUEST;
            case NORD_EST -> Plante.ID.STOCK_NORD_EST;
            case SUD -> Plante.ID.STOCK_SUD;
            case SUD_EST -> Plante.ID.STOCK_SUD_EST;
            case SUD_OUEST -> Plante.ID.STOCK_SUD_OUEST;
             */
            default -> null;
        };
    }
}
