package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Point;
import org.arig.robot.model.Rang;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandGradinEquipe extends AbstractDeposeGradin {

  private static final int CENTER_X = 1225;

  @Override
  protected ConstructionArea constructionArea() {
    return rs.grandGradinEquipe();
  }

  @Override
  protected Point rangPosition(Rang rang) {
    return switch (rang) {
      case RANG_1 -> new Point(getX(CENTER_X), EurobotConfig.rang1Coord);
      case RANG_2 -> new Point(getX(CENTER_X), EurobotConfig.rang2Coord);
      case RANG_3 -> new Point(getX(CENTER_X), EurobotConfig.rang3Coord);
      case CONSTRUCTION -> new Point(getX(CENTER_X), EurobotConfig.rang4Coord);
    };
  }
}
