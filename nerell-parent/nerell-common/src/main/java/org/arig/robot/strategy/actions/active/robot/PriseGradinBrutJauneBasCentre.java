package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseGradinBrutJauneBasCentre extends AbstractPriseGradinBrutJaune {

  @Override
  protected GradinBrut.ID gradinId() {
    return GradinBrut.ID.JAUNE_BAS_CENTRE;
  }

  @Override
  public Point entryPoint() {
    Point entry = gradin().clone();
    entry.addDeltaY(EurobotConfig.offsetGradin);
    return entry;
  }
}
