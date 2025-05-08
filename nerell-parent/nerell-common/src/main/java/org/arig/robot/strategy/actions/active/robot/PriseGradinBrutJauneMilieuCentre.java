package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseGradinBrutJauneMilieuCentre extends AbstractPriseGradinBrutJaune {

  @Override
  protected GradinBrut.ID gradinId() {
    return GradinBrut.ID.JAUNE_MILIEU_CENTRE;
  }

  @Override
  public Point entryPoint() {
    Point entryNord = gradin().clone();
    entryNord.addDeltaY(EurobotConfig.offsetGradin);
    double distanceNord = tableUtils.distance(entryNord);

    Point entrySud = gradin().clone();
    entrySud.addDeltaY(-EurobotConfig.offsetGradin);
    double distanceSud = tableUtils.distance(entrySud);

    if (distanceNord < distanceSud) {
      return entryNord;
    } else {
      return entrySud;
    }
  }
}
