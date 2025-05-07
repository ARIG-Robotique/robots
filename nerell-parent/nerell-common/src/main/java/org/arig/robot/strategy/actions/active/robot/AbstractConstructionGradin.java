package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.services.AbstractNerellFaceService;
import org.arig.robot.services.NerellFaceWrapper;
import org.arig.robot.strategy.actions.AbstractNerellAction;

@Slf4j
public abstract class AbstractConstructionGradin extends AbstractNerellAction {

  protected abstract ConstructionArea constructionArea();

  @Override
  public String name() {
    return EurobotConfig.ACTION_DEPOSE_GRADIN_PREFIX + constructionArea().name();
  }

  @Override
  public int order() {
    return 4 + 8 + tableUtils.alterOrder(entryPoint()); // Deux niveaux
  }

  @Override
  public boolean isValid() {
    if (faceWrapper.getConstructionFace(constructionArea().nbEtage()) == null) {
      return false;
    }

    return isTimeValid() && rs.getRemainingTime() > EurobotConfig.validTimeEchappement
        && constructionArea().isEmpty();
  }

  @Override
  public int executionTimeMs() {
    return 0;
  }

  @Override
  public void execute() {
    mv.setVitessePercent(100, 100);

    try {
      mv.pathTo(entryPoint());

      NerellFaceWrapper.Face face = faceWrapper.getConstructionFace(1);
      AbstractNerellFaceService faceService = faceWrapper.getFaceService(face);

      log.info("Depose gradin");
      // 1. Zone vide. On construit.
      //    a. Si limit a deux etages, on récupère la face dispo avec deux étages.
      //    b. Sinon on construit un étage.

    } catch (NoPathFoundException | AvoidingException e) {
      log.warn("Erreur prise {} : {}", name(), e.toString());
      updateValidTime();
    }
  }
}
