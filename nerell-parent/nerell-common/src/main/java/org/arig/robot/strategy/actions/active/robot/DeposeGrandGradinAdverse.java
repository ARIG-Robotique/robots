package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Point;
import org.arig.robot.model.Rang;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandGradinAdverse extends AbstractDeposeGradin {

  private static final int CENTER_Y = 875;

  @Override
  protected void applyOffsetRangPosition(Point position) {
    if (rs.team() == Team.JAUNE) {
      position.addDeltaX(-EurobotConfig.offsetDeposeGradin);
    } else {
      position.addDeltaX(EurobotConfig.offsetDeposeGradin);
    }
  }

  @Override
  protected ConstructionArea constructionArea() {
    return rs.grandGradinAdverse();
  }

  @Override
  protected Point rangPosition(Rang rang) {
    // Use tableUtils directly to reverse the game inverse in this action
    return switch (rang) {
      case RANG_1 -> new Point(tableUtils.getX(rs.team() == Team.JAUNE, EurobotConfig.rang1Coord), CENTER_Y);
      case RANG_2 -> new Point(tableUtils.getX(rs.team() == Team.JAUNE, EurobotConfig.rang2Coord), CENTER_Y);
      case RANG_3 -> new Point(tableUtils.getX(rs.team() == Team.JAUNE, EurobotConfig.rang3Coord), CENTER_Y);
    };
  }
}
