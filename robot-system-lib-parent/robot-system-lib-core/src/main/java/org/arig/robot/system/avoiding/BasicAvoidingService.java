package org.arig.robot.system.avoiding;

import lombok.extern.slf4j.Slf4j;

/**
 * Evitement basique : arret complet si obstacle sans reprise
 */
@Slf4j
public class BasicAvoidingService extends AbstractAvoidingService {

  private boolean currentObstacle = false;

  @Override
  protected void processAvoiding() {
    boolean hasObstacle = hasProximite();

    // Log de détection avec un sémaphore
    if (hasObstacle && !currentObstacle) {
      log.info("Attente que l'obstacle sans aille ...");
      currentObstacle = true;
    }
    if (hasObstacle) {
      trajectoryManager.obstacleFound();
    }

    if (!hasObstacle && currentObstacle) {
      log.info("L'obstacle à disparu on relance le cycle.");
    }

    if (!hasObstacle) {
      // 3.4 On relance le bouzin
      trajectoryManager.obstacleNotFound();
      currentObstacle = false;
    }
  }
}
