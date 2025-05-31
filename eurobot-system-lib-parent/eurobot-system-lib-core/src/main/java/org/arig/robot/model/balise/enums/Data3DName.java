package org.arig.robot.model.balise.enums;

import org.arig.robot.model.Team;

public enum Data3DName {
  INCONNU,
  //  BOARD,
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
  CARRE,
  ROND,
  STAR,

  GROS_BLEU_CENTRE,
  GROS_BLEU_GAUCHE,
  PETIT_BLEU_DROITE,
  PETIT_BLEU_GAUCHE,

  GROS_JAUNE_CENTRE,
  GROS_JAUNE_DROITE,
  PETIT_JAUNE_DROITE,
  PETIT_JAUNE_GAUCHE,

  JAUNE_RESERVE,
  JAUNE_HAUT_GAUCHE,
  JAUNE_MILIEU_CENTRE,
  JAUNE_BAS_GAUCHE,
  JAUNE_BAS_CENTRE,

  BLEU_RESERVE,
  BLEU_HAUT_DROITE,
  BLEU_MILIEU_CENTRE,
  BLEU_BAS_DROITE,
  BLEU_BAS_CENTRE;

  public static Team getRobotTeam(Data3DName data3DName) {
    return switch (data3DName) {
      case BLEU_1, BLEU_2, BLEU_3, BLEU_4, BLEU_5 -> Team.BLEU;
      case JAUNE_6, JAUNE_7, JAUNE_8, JAUNE_9, JAUNE_10 -> Team.JAUNE;
      default -> null;
    };
  }
}
