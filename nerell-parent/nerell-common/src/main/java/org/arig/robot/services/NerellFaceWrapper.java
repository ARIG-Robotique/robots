package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellRobotStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NerellFaceWrapper {

  public enum Face {
    AVANT, ARRIERE
  }

  private final NerellRobotStatus robotStatus;
  private final NerellFaceAvantService faceAvantService;
  private final NerellFaceArriereService faceArriereService;

  public Face getEmptyFace() {
    if (robotStatus.faceAvant().isEmpty()) {
      return Face.AVANT;
    //} else if (robotStatus.faceArriere().isEmpty()) {
    //  return Face.ARRIERE;
    } else {
      return null;
    }
  }

  public Face getConstructionFace(int nbEtageRequis) {
    if (robotStatus.faceAvant().isEmpty() && robotStatus.faceArriere().isEmpty()) {
      // Aucune face n'est rempli pour faire une construction
      return null;
    }

    // Récupération de la face avec le nombre d'étage requis s'il y en a une
    if (robotStatus.faceAvant().nbEtageConstructible() == nbEtageRequis) {
      return Face.AVANT;
    } else if (robotStatus.faceArriere().nbEtageConstructible() == nbEtageRequis) {
      return Face.ARRIERE;
    }

    // Sinon on regarde si on peut construire un étage avec une des faces
    if (robotStatus.faceAvant().nbEtageConstructible() > 0) {
      return Face.AVANT;
    } else if (robotStatus.faceArriere().nbEtageConstructible() > 0) {
      return Face.ARRIERE;
    }

    // Rien ne correspond, c'est louche
    log.warn("Aucune face ne correspond à la construction pour {} étage(s). Bizarre !", nbEtageRequis);
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
