package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Face;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NerellFaceWrapper {

  private final ConvertionRobotUnit convRobot;
  private final Position currentPosition;
  private final NerellRobotStatus robotStatus;
  private final NerellFaceAvantService faceAvantService;
  private final NerellFaceArriereService faceArriereService;

  public Face getEmptyFace(GradinBrut.ID gradinId) {
    // Ajouter une priorité sur la face déja orienté
    boolean avantEmpty = robotStatus.faceAvant().isEmpty();
    boolean arriereEmpty = robotStatus.faceArriere().isEmpty();

    if (!avantEmpty && !arriereEmpty) {
      return null;
    }

    if (avantEmpty && !arriereEmpty) {
      return Face.AVANT;
    } else if (!avantEmpty) {
      return Face.ARRIERE;
    }

    double angleRobot = convRobot.pulseToDeg(currentPosition.getAngle());
    double posY = convRobot.pulseToMm(currentPosition.getPt().getY());

    if (gradinId == GradinBrut.ID.JAUNE_RESERVE || gradinId == GradinBrut.ID.BLEU_RESERVE) {
      if (angleRobot >= 0 && angleRobot < 180) {
        return Face.AVANT;
      } else {
        return Face.ARRIERE;
      }
    }

    if (gradinId == GradinBrut.ID.JAUNE_BAS_CENTRE || gradinId == GradinBrut.ID.BLEU_BAS_CENTRE) {
      if (angleRobot >= 0 && angleRobot < 180) {
        return Face.ARRIERE;
      } else {
        return Face.AVANT;
      }
    }

    if (gradinId == GradinBrut.ID.JAUNE_HAUT_GAUCHE || gradinId == GradinBrut.ID.JAUNE_BAS_GAUCHE) {
      if (angleRobot <= -90 || angleRobot > 90) {
        return Face.AVANT;
      } else {
        return Face.ARRIERE;
      }
    }

    if (gradinId == GradinBrut.ID.BLEU_HAUT_DROITE || gradinId == GradinBrut.ID.BLEU_BAS_DROITE) {
      if (angleRobot <= -90 || angleRobot > 90) {
        return Face.ARRIERE;
      } else {
        return Face.AVANT;
      }
    }

    if (gradinId == GradinBrut.ID.JAUNE_MILIEU_CENTRE || gradinId == GradinBrut.ID.BLEU_MILIEU_CENTRE) {
      if (angleRobot >= 0 && angleRobot < 180) {
        return posY < 950 ? Face.AVANT : Face.ARRIERE;
      } else {
        return posY < 950 ? Face.ARRIERE : Face.AVANT;
      }
    }

    return null;
  }

  public AbstractNerellFaceService getFaceService(final Face face) {
    if (face == null) {
      return null;
    }
    return switch (face) {
      case AVANT -> faceAvantService;
      case ARRIERE -> faceArriereService;
    };
  }
}
