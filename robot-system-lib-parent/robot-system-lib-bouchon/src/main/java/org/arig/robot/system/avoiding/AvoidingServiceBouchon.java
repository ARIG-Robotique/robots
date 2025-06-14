package org.arig.robot.system.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.LidarService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO à reprendre pour faire des tests, vielle implémentation supprimée
 */
@Slf4j
public class AvoidingServiceBouchon implements AvoidingService {

  @Autowired
  private LidarService lidarService;

  @Override
  public void process() {
    lidarService.refreshDetectedPoints();

    if (lidarService.mustCleanup()) {
      lidarService.refreshObstacles();
    }
  }

  @Override
  public void setSafeAvoidance(boolean enabled) {

  }
}
