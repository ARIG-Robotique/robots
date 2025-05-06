package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.NerellRobotStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NerellFaceWrapper {

  public enum Face {
    AVANT, ARRIERE
  }

  private final NerellRobotStatus robotStatus;
  private final NerellFaceAvantService faceAvantService;
  private final NerellFaceArriereService faceArriereService;

  public Face getFace() {
    if (robotStatus.faceAvant().isEmpty()) {
      return Face.AVANT;
    } else if (robotStatus.faceArriere().isEmpty()) {
      return Face.ARRIERE;
    } else {
      return null;
    }
  }

  public AbstractNerellFaceService getFaceService(final Face face ) {
    return switch (face) {
      case AVANT -> faceAvantService;
      case ARRIERE -> faceArriereService;
    };
  }
}
