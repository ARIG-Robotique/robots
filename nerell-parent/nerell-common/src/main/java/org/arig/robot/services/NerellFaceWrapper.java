package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellFace;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NerellFaceWrapper {

  private final Position currentPosition;
  private final NerellRobotStatus robotStatus;
  private final NerellFaceAvantService faceAvantService;
  private final NerellFaceArriereService faceArriereService;

  public NerellFace getEmptyFace(boolean useTwoFaces) {
    // Ajouter une priorité sur la face déja orienté
    boolean avantEmpty = robotStatus.faceAvant().isEmpty();
    boolean arriereEmpty = robotStatus.faceArriere().isEmpty();
    if (avantEmpty) {
      return NerellFace.AVANT;
    }
    if (useTwoFaces && arriereEmpty) {
      return NerellFace.ARRIERE;
    }

    return null;
  }

  public AbstractNerellFaceService getFaceService(final NerellFace face) {
    if (face == null) {
      return null;
    }
    return switch (face) {
      case AVANT -> faceAvantService;
      case ARRIERE -> faceArriereService;
    };
  }
}
