package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.services.AbstractNerellFaceService;
import org.arig.robot.services.NerellFaceWrapper;
import org.arig.robot.strategy.actions.AbstractNerellAction;

@Slf4j
public abstract class AbstractPriseGradinBrut extends AbstractNerellAction {

  protected abstract GradinBrut.ID gradinId();

  protected GradinBrut gradin() {
    return rs.gradinBrutStocks().get(gradinId());
  }

  @Override
  public void refreshCompleted() {
    if (!gradin().present() || gradin().bloque()) {
      complete(true);
    }
  }

  @Override
  public String name() {
    return EurobotConfig.ACTION_PRISE_GRADIN_BRUT_PREFIX + gradinId().name();
  }

  @Override
  public int order() {
    return 4 + 8 + tableUtils.alterOrder(entryPoint()); // Deux niveaux
  }

  @Override
  public boolean isValid() {
    if (faceWrapper.getEmptyFace(rs.useTwoFaces()) == null) {
      return false;
    }

    // Forcer le remplissage des deux faces si on est en mode deux faces avant un certains moment de la fin
    // Ajouter aussi le paramètre de la limite a deux niveaux ??
    if (rs.useTwoFaces() && rs.getRemainingTime() >= EurobotConfig.validPriseDeuxFacesPleineRemainingTime && !rs.faceAvant().isEmpty() && !rs.faceArriere().isEmpty()) {
      return false;
    }

    return isTimeValid() && rs.getRemainingTime() > EurobotConfig.validPriseRemainingTime
        && gradin().present() && !gradin().bloque();
  }

  @Override
  public int executionTimeMs() {
    return 0;
  }

  @Override
  public void execute() {
    mv.setVitessePercent(100, 100);

    AbstractNerellFaceService.PriseGradinState priseGradinState = null;
    try {
      mv.pathTo(entryPoint());

      GradinBrut gradin = gradin();
      NerellFaceWrapper.Face face = faceWrapper.getEmptyFace(rs.useTwoFaces());
      AbstractNerellFaceService faceService = faceWrapper.getFaceService(face);

      faceService.preparePriseGradinBrut(gradin);
      priseGradinState = faceService.prendreGradinBrutStockTiroir();
      log.info("Prise de gradin brut {} : {}", gradin.id(), priseGradinState);
      if (priseGradinState == AbstractNerellFaceService.PriseGradinState.OK) {
        gradin().setGradinPris();
        if (gradin.bordure()) {
          if (face == NerellFaceWrapper.Face.AVANT) {
            mv.reculeMM(100);
          } else {
            mv.avanceMM(100);
          }
        }
      } else {
        gradin().setGradinBloque();
      }
      complete();

    } catch (NoPathFoundException | AvoidingException e) {
      log.warn("Erreur prise {} : {}", name(), e.toString());
      updateValidTime();
    }
  }
}
