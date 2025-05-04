package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;

@Slf4j
public abstract class AbstractPriseGradinBrut extends AbstractNerellAction {

  protected abstract GradinBrut.ID gradinId();

  protected GradinBrut gradin() {
    return rs.gradinBrutStocks().get(gradinId());
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
    return isTimeValid() && rs.getRemainingTime() > EurobotConfig.validTimePrise
        && gradin().present() && !gradin().bloque();
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

      // Aligne la face qui est disponible
      if (!rs.faceAvantFull()) {
        mv.alignFrontTo(gradin());
      } else {
        mv.alignBackTo(gradin());
      }

      ThreadUtils.sleep(3000);

      complete();

    } catch (NoPathFoundException | AvoidingException e) {
      log.warn("Erreur prise {} : {}", name(), e.toString());
      updateValidTime();
    }
  }
}
