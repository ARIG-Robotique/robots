package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandGradinAdverse extends AbstractDeposeGradin {

  private static final int CENTER_Y = 875;

  @Override
  public Point entryPoint() {
    Point entry = super.entryPoint();
    entry.addDeltaY(-EurobotConfig.offsetDeposeGradin);
    entry.addDeltaX(EurobotConfig.offsetDeposeGradin);
    return entry;
  }

  @Override
  protected ConstructionArea constructionArea() {
    return rs.grandGradinAdverse();
  }

  @Override
  protected Point rangPosition(ConstructionArea.Rang rang) {
    return switch (rang) {
      case RANG_1 -> new Point(getX(EurobotConfig.rang1Coord), CENTER_Y);
      case RANG_2 -> new Point(getX(EurobotConfig.rang2Coord), CENTER_Y);
      case RANG_3 -> new Point(getX(EurobotConfig.rang3Coord), CENTER_Y);
    };
  }
}
