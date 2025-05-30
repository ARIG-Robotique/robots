package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Point;
import org.arig.robot.model.Rang;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposePetitGradinAdverse extends AbstractDeposeGradin {

  private static final int CENTER_X = 2775;

  @Override
  protected ConstructionArea constructionArea() {
    return rs.petitGradinAdverse();
  }

  @Override
  public boolean isValid() {
    if (rs.eviterCoteAdverse()) {
      return false;
    }
    return super.isValid();
  }

  @Override
  public void refreshCompleted() {
    if (rs.eviterCoteAdverse()) {
      log.info("DeposePetitGradinAdverse: Complete car coté adverse désactivé");
      complete(true);
    }
  }

  @Override
  protected Point rangPosition(Rang rang) {
    if (rang == Rang.CONSTRUCTION) {
      return new Point(getX(CENTER_X), EurobotConfig.rang2Coord);
    }
    return new Point(getX(CENTER_X), EurobotConfig.rang1Coord);
  }
}
