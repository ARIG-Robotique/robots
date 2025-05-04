package org.arig.robot.strategy.actions.active.robot;

import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Team;

public abstract class AbstractPriseGradinBrutBleu extends AbstractPriseGradinBrut {

  @Override
  public boolean isValid() {
    if (rs.team() == Team.JAUNE && rs.getRemainingTime() > EurobotConfig.matchTimeMs / 2)
      return false;

    return super.isValid();
  }
}
