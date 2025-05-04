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
  public void refreshCompleted() {
    if (!gradin().present()) {
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

      mv.alignFrontTo(gradin());

      // a. Aligne la face qui est disponible
      // b. Prepare la prise
      // c. Avance jusqu'à la prise (capteurs ??)
      // d1. Si bordure
      // d1.a Mise en stock planche
      // d1.b Avance en calage bordure (simple ou double)
      // d1.c Prise des colonnes dans robot (bas)
      // d2. Si pas de bordure
      // d2.a Mise en stock planche
      // d2.b Avance diam (gradin) + 50
      // d2.c Prise des colonnes dans robot (bas)
      // e. Prise des colonnes dans robot (haut)
      // f. Recule 100 mm

      ThreadUtils.sleep(3000);
      gradin().setGradinPris();
      complete();

    } catch (NoPathFoundException | AvoidingException e) {
      log.warn("Erreur prise {} : {}", name(), e.toString());
      updateValidTime();
    }
  }
}
