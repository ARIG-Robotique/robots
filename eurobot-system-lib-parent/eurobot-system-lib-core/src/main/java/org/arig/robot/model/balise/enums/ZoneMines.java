package org.arig.robot.model.balise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.arig.robot.communication.socket.balise.DataQueryData;
import org.arig.robot.communication.socket.balise.ZoneQueryData;
import org.arig.robot.model.Team;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum ZoneMines {
    SOLAR_PANEL_1(275, 50, 270, 75, Team.BLEU),
    SOLAR_PANEL_2(500, 50, 270, 75, Team.BLEU),
    SOLAR_PANEL_3(725, 50, 270, 75, Team.BLEU),
    SOLAR_PANEL_4(1275, 50, 270, 75, null),
    SOLAR_PANEL_5(1500, 50, 270, 75, null),
    SOLAR_PANEL_6(1725, 50, 270, 75, null),
    SOLAR_PANEL_7(2225, 50, 270, 75, Team.JAUNE),
    SOLAR_PANEL_8(2500, 50, 270, 75, Team.JAUNE),
    SOLAR_PANEL_9(2725, 50, 270, 75, Team.JAUNE),
    JAUNE_NORD(2240, 1950, 270, 50, Team.JAUNE),
    JAUNE_MILIEU(2950, 1340, 50, 270, Team.JAUNE),
    JAUNE_SUD(50, 565, 50, 270, Team.JAUNE),
    BLEU_NORD(760, 1950, 270, 50, Team.BLEU),
    BLEU_MILIEU(50, 1340, 50, 270, Team.BLEU),
    BLEU_SUD(2950, 565, 50, 270, Team.BLEU);

    private final int cx;

    private final int cy;

    private final int dx;

    private final int dy;

    private final Team team;

    public static ZoneQueryData.Zone toQueryZone(ZoneMines zone) {
        return new ZoneQueryData.Zone(zone.name(), zone.cx, zone.cy, zone.dx, zone.dy);
    }

    public static ZoneMines getPanneau(int nb) {
        return switch (nb) {
            case 1 -> SOLAR_PANEL_1;
            case 2 -> SOLAR_PANEL_2;
            case 3 -> SOLAR_PANEL_3;
            case 4 -> SOLAR_PANEL_4;
            case 5 -> SOLAR_PANEL_5;
            case 6 -> SOLAR_PANEL_6;
            case 7 -> SOLAR_PANEL_7;
            case 8 -> SOLAR_PANEL_8;
            case 9 -> SOLAR_PANEL_9;
            default -> null;
        };
    }

    public Rectangle toRectangle() {
        return new Rectangle(this.cx + this.dx / 2, this.cy + this.dy / 2, this.cx - this.dx / 2, this.cx - this.dx / 2);
    }
}
