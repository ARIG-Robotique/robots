package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseGradinBrutJauneHautGauche extends AbstractPriseGradinBrutJaune {

  @Override
  protected GradinBrut.ID gradinId() {
    return GradinBrut.ID.JAUNE_HAUT_GAUCHE;
  }

  @Override
  public Point entryPoint() {
    Point entry = gradin().clone();
    entry.addDeltaX(EurobotConfig.offsetPriseGradin);
    return entry;
  }
}
