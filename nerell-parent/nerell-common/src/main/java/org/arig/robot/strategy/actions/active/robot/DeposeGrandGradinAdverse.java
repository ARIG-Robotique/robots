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
  private static final int ENTRY_X = 3000 - EurobotConfig.offsetGradin;
  private static final int RANG_1_X = 75;

  @Override
  public Point entryPoint() {
    ConstructionArea.Rang rang = constructionArea().getFirstConstructibleRang(rs.limiter2Etages());
    return switch (rang) {
      case RANG_1 -> new Point(getX(RANG_1_X + ENTRY_X), CENTER_Y);
      case RANG_2 -> new Point(getX(2 * RANG_1_X + ENTRY_X), CENTER_Y);
      case RANG_3 -> new Point(getX(3 * RANG_1_X + ENTRY_X), CENTER_Y);
    };
  }

  @Override
  protected ConstructionArea constructionArea() {
    return rs.grandGradinAdverse();
  }

  @Override
  protected Point rangPosition(ConstructionArea.Rang rang) {
    return switch (rang) {
      case RANG_1 -> new Point(getX(RANG_1_X), CENTER_Y);
      case RANG_2 -> new Point(getX(2 * RANG_1_X), 2 * CENTER_Y);
      case RANG_3 -> new Point(getX(3 * RANG_1_X), 3 * CENTER_Y);
    };
  }
}
