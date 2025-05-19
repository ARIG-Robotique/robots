package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandGradinAdverse extends AbstractDeposeGradin {

  private static final int CENTER_Y = 875;

  @Override
  public Point entryPoint() {
    Point entry = super.entryPoint();
    entry.addDeltaY(-EurobotConfig.offsetDeposeGradin); // Reset opération super.entryPoint
    if (rs.team() == Team.JAUNE) {
      entry.addDeltaX(-EurobotConfig.offsetDeposeGradin);
    } else {
      entry.addDeltaX(EurobotConfig.offsetDeposeGradin);
    }
    return entry;
  }

  @Override
  protected ConstructionArea constructionArea() {
    return rs.grandGradinAdverse();
  }

  @Override
  protected Point rangPosition(ConstructionArea.Rang rang) {
    // Use tableUtils directly to reverse the game inverse in this action
    return switch (rang) {
      case RANG_1 -> new Point(tableUtils.getX(rs.team() == Team.JAUNE, EurobotConfig.rang1Coord), CENTER_Y);
      case RANG_2 -> new Point(tableUtils.getX(rs.team() == Team.JAUNE, EurobotConfig.rang2Coord), CENTER_Y);
      case RANG_3 -> new Point(tableUtils.getX(rs.team() == Team.JAUNE, EurobotConfig.rang3Coord), CENTER_Y);
    };
  }
}
