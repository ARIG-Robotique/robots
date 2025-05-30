package org.arig.robot.model.balise.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.arig.robot.communication.socket.balise.ZoneQueryData;
import org.arig.robot.model.Team;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum ZoneMines {
  JAUNE_RESERVE(625, 1675, 400, 100, Team.JAUNE),
  JAUNE_HAUT_GAUCHE(25, 1125, 100, 400, Team.JAUNE),
  JAUNE_MILIEU_CENTRE(900, 900, 400, 100, Team.JAUNE),
  JAUNE_BAS_GAUCHE(25, 200, 100, 400, Team.JAUNE),
  JAUNE_BAS_CENTRE(575, 200, 400, 100, Team.JAUNE),

  BLEU_RESERVE(1975, 1675, 400, 100, Team.BLEU),
  BLEU_HAUT_DROITE(2875, 1125, 100, 400, Team.BLEU),
  BLEU_MILIEU_CENTRE(1700, 900, 400, 100, Team.BLEU),
  BLEU_BAS_DROITE(2875, 200, 100, 400, Team.BLEU),
  BLEU_BAS_CENTRE(2025, 200, 400, 300, Team.BLEU);

  private final int cx;

  private final int cy;

  private final int dx;

  private final int dy;

  private final Team team;

  public static ZoneQueryData.Zone toQueryZone(ZoneMines zone) {
    return new ZoneQueryData.Zone(zone.name(), zone.cx, zone.cy, zone.dx, zone.dy);
  }

  public Rectangle toRectangle() {
    return new Rectangle(this.cx + this.dx / 2, this.cy + this.dy / 2, this.cx - this.dx / 2, this.cx - this.dx / 2);
  }
}
