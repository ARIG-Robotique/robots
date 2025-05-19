package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Team;

@Slf4j
public abstract class AbstractPriseGradinBrutJaune extends AbstractPriseGradinBrut {

  @Override
  public boolean isValid() {
    if (rs.team() == Team.BLEU && rs.getRemainingTime() > EurobotConfig.validPriseAdverseRemainingTime)
      return false;

    return super.isValid();
  }

  @Override
  public void refreshCompleted() {
    if (rs.team() == Team.BLEU && rs.eviterCoteAdverse()) {
      log.info("PriseGradinBrutJauneReserve: complete car BLEU avec 'Eviter cote adverse'");
      complete(true);
    }
    super.refreshCompleted();
  }
}
