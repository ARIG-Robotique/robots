package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseGradinBrutBleuReserve extends AbstractPriseGradinBrutBleu {

  @Override
  protected GradinBrut.ID gradinId() {
    return GradinBrut.ID.BLEU_RESERVE;
  }

  @Override
  public Point entryPoint() {
    Point entry = gradin().clone();
    entry.addDeltaY(-EurobotConfig.offsetGradinBrut);
    return entry;
  }

  @Override
  public void refreshCompleted() {
    if (rs.team() == Team.JAUNE) {
      log.info("PriseGradinBrutBleuReserve: complete car JAUNE");
      complete(true);
    }
    super.refreshCompleted();
  }
}
