package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.BackstageState;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.Team;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetourBackstage extends AbstractNerellAction {

  private static final int FINAL_X = 350;
  private static final int ENTRY_Y = 1350;
  private final Position position;

  public RetourBackstage(Position position) {
    super();
    this.position = position;
  }

  @Override
  public String name() {
    return EurobotConfig.ACTION_RETOUR_BACKSTAGE;
  }

  @Override
  public int executionTimeMs() {
    return 0;
  }

  @Override
  public Point entryPoint() {
    return new Point(getX(FINAL_X), ENTRY_Y);
  }

  @Override
  public int order() {
    return 10 + tableUtils.alterOrder(entryPoint());
  }

  @Override
  public boolean isValid() {
    return ilEstTempsDeRentrer();
  }

  @Override
  public void execute() {
    mv.setVitessePercent(100, 100);

    try {
      log.info("Go backstage");
      groups.forEach(g -> g.backstage(BackstageState.IN_MOVE));

      mv.pathTo(entryPoint());
      if (position.getAngle() > 0) {
        // Face Avant
        servosNerell.ascenseurAvantStock(false);
        if (rs.team() == Team.JAUNE) {
          mv.gotoOrientationDeg(180 - 35);
        } else {
          mv.gotoOrientationDeg(35);
        }
        servosNerell.groupePincesAvantPrise(false);
      } else {
        // Face Arrière
        servosNerell.ascenseurArriereStock(false);
        if (rs.team() == Team.JAUNE) {
          mv.gotoOrientationDeg(-35);
        } else {
          mv.gotoOrientationDeg(-180 + 35);
        }
        servosNerell.groupePincesArrierePrise(false);
      }
      log.info("Arrivée au backstage");
      groups.forEach(g -> g.backstage(BackstageState.TARGET_REACHED));
      complete(true);
      rs.disableAvoidance();

      ThreadUtils.sleep((int) rs.getRemainingTime());

    } catch (NoPathFoundException | AvoidingException e) {
      log.warn("Impossible d'aller au backstage : {}", e.toString());
    }

    if (!isCompleted()) {
      log.error("Erreur d'exécution de l'action");
      updateValidTime();
      groups.forEach(g -> g.backstage(BackstageState.OUTSIDE));
    }
  }
}
