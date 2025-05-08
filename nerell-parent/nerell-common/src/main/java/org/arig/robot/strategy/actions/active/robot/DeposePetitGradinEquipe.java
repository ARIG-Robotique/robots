package org.arig.robot.strategy.actions.active.robot;

import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Point;

public class DeposePetitGradinEquipe extends AbstractDeposeGradin {

  private static final int CENTER_X = 775;
  private static final int ENTRY_Y = EurobotConfig.offsetGradin;
  private static final int RANG_1_Y = 75;

  @Override
  public Point entryPoint() {
    return new Point(getX(CENTER_X), RANG_1_Y);
  }

  @Override
  protected ConstructionArea constructionArea() {
    return rs.petitGradinEquipe();
  }

  @Override
  protected Point rangPosition(ConstructionArea.Rang rang) {
    return new Point(getX(CENTER_X), RANG_1_Y);
  }
}
