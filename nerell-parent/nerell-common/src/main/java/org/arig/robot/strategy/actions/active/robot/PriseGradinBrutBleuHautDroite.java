package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseGradinBrutBleuHautDroite extends AbstractPriseGradinBrutBleu {

  @Override
  protected GradinBrut.ID gradinId() {
    return GradinBrut.ID.BLEU_HAUT_DROITE;
  }

  @Override
  public Point entryPoint() {
    Point entry = gradin().clone();
    entry.addDeltaX(-EurobotConfig.offsetGradinBrut);
    return entry;
  }
}
