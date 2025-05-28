package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeBanderole extends AbstractNerellAction {

  @Override
  public int executionTimeMs() {
    return 0;
  }

  @Override
  public Point entryPoint() {
    return null;
  }

  @Override
  public String name() {
    return EurobotConfig.ACTION_DEPOSE_BANDEROLLE;
  }

  @Override
  public int order() {
    return 1000;
  }

  @Override
  public boolean isValid() {
    return !rs.banderoleDeployee();
  }

  @Override
  public void execute() {
    mv.setVitessePercent(100, 100);
    rs.disableAvoidance();
    try {
      rs.enableCalage(TypeCalage.AVANT, TypeCalage.FORCE);
      mv.avanceMM(100);
      rs.banderoleDeployee(true);
      servosNerell.ascenseurAvantBas(true);
      mv.reculeMM(100);
      servosNerell.ascenseurAvantRepos(false);

      complete();
    } catch (AvoidingException e) {
      log.warn("Erreur dépose banderole : {}", e.toString());
      updateValidTime();
    }
  }
}
