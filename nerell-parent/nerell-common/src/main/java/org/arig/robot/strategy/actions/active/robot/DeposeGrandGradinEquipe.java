package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandGradinEquipe extends AbstractDeposeGradin {

  private static final int CENTER_X = 1225;
  private static final int ENTRY_Y = EurobotConfig.offsetGradin + EurobotConfig.offsetBanderolle;
  private static final int RANG_1_Y = 75;

  @Override
  public Point entryPoint() {
    ConstructionArea.Rang rang = constructionArea().getFirstConstructibleRang(rs.limiter2Etages());
    return switch (rang) {
      case RANG_1 -> new Point(getX(CENTER_X), RANG_1_Y + ENTRY_Y);
      case RANG_2 -> new Point(getX(CENTER_X), 2 * RANG_1_Y + ENTRY_Y);
      case RANG_3 -> new Point(getX(CENTER_X), 3 * RANG_1_Y + ENTRY_Y);
    };
  }

  @Override
  protected ConstructionArea constructionArea() {
    return rs.grandGradinEquipe();
  }

  @Override
  protected Point rangPosition(ConstructionArea.Rang rang) {
    return switch (rang) {
      case RANG_1 -> new Point(getX(CENTER_X), RANG_1_Y + EurobotConfig.offsetBanderolle);
      case RANG_2 -> new Point(getX(CENTER_X), 2 * RANG_1_Y + EurobotConfig.offsetBanderolle);
      case RANG_3 -> new Point(getX(CENTER_X), 3 * RANG_1_Y + EurobotConfig.offsetBanderolle);
    };
  }
}
