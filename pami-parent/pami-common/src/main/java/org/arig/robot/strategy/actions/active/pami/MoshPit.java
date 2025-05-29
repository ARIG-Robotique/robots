package org.arig.robot.strategy.actions.active.pami;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MoshPit extends AbstractEurobotAction {

  private int tryFinalPoint = 0;
  private boolean firstTime = true;

  public int executionTimeMs() {
    return 0;
  }

  @Override
  public String name() {
    return "Mosh pit";
  }

  @Override
  public Point entryPoint() {
    return null;
  }

  @Override
  public void refreshCompleted() {
    if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
      log.info("Mosh pit pas pour la Superstar");
      complete(true);
    }
  }

  @Override
  public int order() {
    return 10;
  }

  @Override
  public boolean isValid() {
    if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
      return false;
    }
    return isTimeValid() && rs.getRemainingTime() <= EurobotConfig.pamiStartRemainingTimeMs;
  }

  @Override
  public void execute() {
    GotoOption avant = GotoOption.AVANT;
    try {
      mv.setVitessePercent(100, 100);
      rs.disableAvoidance();
      if (firstTime) {
        if (robotName.id() == RobotName.RobotIdentification.PAMI_STAR) {
          mv.avanceMM(380);
          mv.gotoPoint(getX(750), 1370, avant);
        }
        if (robotName.id() == RobotName.RobotIdentification.PAMI_ROND) {
          ThreadUtils.sleep(3000);
          mv.avanceMM(160);
        }
        if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
          ThreadUtils.sleep(6000);
          mv.avanceMM(140);
          mv.gotoPoint(getX(660), 1460, avant);
        }
      }
      rs.enableAvoidance();

      if (robotName.id() == RobotName.RobotIdentification.PAMI_STAR) {
        final int offsetY;
        if (rs.team() == Team.JAUNE) {
          offsetY = 100;
        } else {
          offsetY = -100;
        }
        if (tryFinalPoint == 0) {
          mv.gotoPoint(getX(1800), 1380 + offsetY, avant);
        } else if (tryFinalPoint == 1) {
          mv.gotoPoint(getX(2000), 1300 + offsetY, avant);
        } else if (tryFinalPoint == 2) {
          mv.gotoPoint(getX(1750), 1400 + offsetY, avant);
        }
      }
      if (robotName.id() == RobotName.RobotIdentification.PAMI_ROND) {
        if (tryFinalPoint == 0) {
          mv.gotoPoint(getX(1200), 1350, avant);
        } else if (tryFinalPoint == 1) {
          mv.gotoPoint(getX(1500), 1440, avant);
        } else if (tryFinalPoint == 2) {
          mv.gotoPoint(getX(1200), 1300, avant);
        }
      }
      if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
        if (tryFinalPoint == 0) {
          mv.gotoPoint(getX(920), 1460, avant);
        } else if (tryFinalPoint == 1) {
          mv.gotoPoint(getX(1200), 1290, avant);
        } else if (tryFinalPoint == 2) {
          mv.gotoPoint(getX(880), 1520, avant);
        }
      }

      complete(true);
      ThreadUtils.sleep((int) rs.getRemainingTime());
    } catch (AvoidingException e) {
      log.error("Erreur d'accès au mosh pit", e);
    } finally {
      firstTime = false;
      tryFinalPoint++;
      if (tryFinalPoint > 2) {
        tryFinalPoint = 0;
      }
    }
  }
}
