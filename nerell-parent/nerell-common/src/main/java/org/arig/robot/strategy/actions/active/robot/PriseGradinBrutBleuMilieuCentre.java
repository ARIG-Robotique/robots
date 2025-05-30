package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Point;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseGradinBrutBleuMilieuCentre extends AbstractPriseGradinBrutBleu {

  @Override
  protected GradinBrut.ID gradinId() {
    return GradinBrut.ID.BLEU_MILIEU_CENTRE;
  }

  @Override
  public int order() {
    if (rs.team() == Team.BLEU && rs.strategy() == Strategy.QUALIF) {
      return 500;
    }

    return super.order();
  }

  @Override
  public Point entryPoint() {
    Point entryNord = gradin().clone();
    entryNord.addDeltaY(EurobotConfig.offsetPriseGradin);
    double distanceNord = tableUtils.distance(entryNord);

    Point entrySud = gradin().clone();
    entrySud.addDeltaY(-EurobotConfig.offsetPriseGradin);
    double distanceSud = tableUtils.distance(entrySud);

    if (distanceNord < distanceSud) {
      return entryNord;
    } else {
      return entrySud;
    }
  }
}
