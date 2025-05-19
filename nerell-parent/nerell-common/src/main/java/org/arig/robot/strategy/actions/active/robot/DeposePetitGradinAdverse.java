package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Point;
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
  protected Point rangPosition(ConstructionArea.Rang rang) {
    return new Point(getX(CENTER_X), EurobotConfig.rang1Coord);
  }
}
