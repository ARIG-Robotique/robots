package org.arig.robot.system.avoiding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.average.DoubleValueAverage;
import org.arig.robot.services.PamiIOService;

@Slf4j
@RequiredArgsConstructor
public class GP2D120AvoidingService extends BasicAvoidingService {

  private final PamiIOService pamiIOService;

  private final DoubleValueAverage gaucheAverage = new DoubleValueAverage(1);
  private final DoubleValueAverage centreAverage = new DoubleValueAverage(1);
  private final DoubleValueAverage droiteAverage = new DoubleValueAverage(1);

  @Override
  protected boolean hasProximite() {
    gaucheAverage.filter(pamiIOService.distanceGauche());
    centreAverage.filter(pamiIOService.distanceCentre());
    droiteAverage.filter(pamiIOService.distanceDroite());
    return isObstacle(gaucheAverage.lastResult()) || isObstacle(centreAverage.lastResult()) || isObstacle(droiteAverage.lastResult());
  }

  private boolean isObstacle(double distance) {
    return distance >= 150 && distance <= 430;
  }
}
