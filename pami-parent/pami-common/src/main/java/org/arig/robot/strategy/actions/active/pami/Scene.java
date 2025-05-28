package org.arig.robot.strategy.actions.active.pami;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Scene extends AbstractEurobotAction {

  public int executionTimeMs() {
    return 0;
  }

  @Override
  public String name() {
    return "Scene";
  }

  @Override
  public Point entryPoint() {
    return null;
  }

  @Override
  public void refreshCompleted() {
    if (robotName.id() != RobotName.RobotIdentification.PAMI_TRIANGLE) {
      log.info("Scene que pour la Superstar");
      complete(true);
    }
  }

  @Override
  public int order() {
    return 10;
  }

  @Override
  public boolean isValid() {
    if (robotName.id() != RobotName.RobotIdentification.PAMI_TRIANGLE) {
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
      mv.avanceMM(95);
      mv.gotoPoint(getX(350), 1910, avant);

      // Rampes, on réduit les accels et la vitesse
      //mv.setVitessePercent(100, 30);
      //mv.setRampeOrientationPercent(10, 100);
      //mv.setRampesDistancePercent(10, 100);

      mv.gotoPoint(getX(1200), 1910, avant, GotoOption.SANS_ORIENTATION);

      // On rétablit les accels et la vitesse pour la suite
      //mv.setRampeOrientationPercent(100, 100);
      //mv.setRampesDistancePercent(100, 100);
      //mv.setVitessePercent(100, 100);

      mv.gotoOrientationDeg(-90);
      mv.avanceMM(300);

      // Bord de scene, on réduit les accels et la vitesse
      mv.setVitessePercent(10, 10);
      mv.setRampeOrientationPercent(10, 100);
      mv.setRampesDistancePercent(10, 100);

      rs.enableCalageTempo(2000);
      mv.avanceMM(70);
      //mv.setVitessePercent(20, 100);
      //rs.enableCalage(TypeCalage.PRISE_PRODUIT_SOL_AVANT);
      //mv.avanceMM(42);

      rs.disableAsserv();
      complete(true);

      ThreadUtils.sleep((int) rs.getRemainingTime());
    } catch (AvoidingException e) {
      log.error("Erreur d'accès a la scene", e);
    }
  }
}
