package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Team;

@Slf4j
public abstract class AbstractPriseGradinBrutBleu extends AbstractPriseGradinBrut {

  @Override
  public boolean isValid() {
    if (rs.team() == Team.JAUNE && rs.getRemainingTime() > EurobotConfig.validPriseAdverseRemainingTime)
      return false;

    return super.isValid();
  }

  @Override
  public void refreshCompleted() {
    if (rs.team() == Team.JAUNE && rs.eviterCoteAdverse()) {
      log.info("GradinBrutBleu: complete car JAUNE avec 'Eviter cote adverse'");
      complete(true);
    }
    super.refreshCompleted();
  }
}
