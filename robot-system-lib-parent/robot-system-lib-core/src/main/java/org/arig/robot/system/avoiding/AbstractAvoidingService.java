package org.arig.robot.system.avoiding;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.services.LidarService;
import org.arig.robot.services.TrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractAvoidingService implements AvoidingService {

  @Autowired
  protected TrajectoryManager trajectoryManager;

  @Autowired
  protected LidarService lidarService;

  @Autowired
  protected CommandeRobot cmdRobot;

  @Autowired
  protected AbstractRobotStatus rs;

  @Autowired
  protected RobotConfig robotConfig;

  @Setter
  private boolean safeAvoidance = true;

  protected abstract void processAvoiding();

  public final void process() {
    lidarService.refreshDetectedPoints();

    if (lidarService.mustCleanup()) {
      lidarService.refreshObstacles();
    }

    trajectoryManager.setLowSpeed(needLowSpeed());

    processAvoiding();
  }

  protected boolean hasProximite() {
    int seuil = safeAvoidance ? robotConfig.pathFindingSeuilProximiteSafe() : robotConfig.pathFindingSeuilProximite();
    int seuilArig = robotConfig.pathFindingSeuilProximiteArig();
    return lidarService.getDetectedPointsMm().parallelStream()
      .anyMatch(pt -> checkValidPointForSeuil(pt, lidarService.isOtherRobot(pt) ? seuilArig : seuil));
  }

  protected boolean needLowSpeed() {
    int seuil = 2 * (safeAvoidance ? robotConfig.pathFindingSeuilProximiteSafe() : robotConfig.pathFindingSeuilProximite());
    int seuilArig = 2 * robotConfig.pathFindingSeuilProximiteArig();
    return lidarService.getDetectedPointsMm().parallelStream()
      .anyMatch(pt -> checkValidPointForSeuil(pt, lidarService.isOtherRobot(pt) ? seuilArig : seuil));
  }

  private boolean checkValidPointForSeuil(Point pt, int seuilMm) {
    long dX = (long) (pt.getX() - trajectoryManager.currentXMm());
    long dY = (long) (pt.getY() - trajectoryManager.currentYMm());
    double distanceMm = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

    if (distanceMm > seuilMm) {
      return false;
    }

    double alpha = Math.toDegrees(Math.atan2(dY, dX));
    double dA = alpha - trajectoryManager.currentAngleDeg();
    if (dA > 180) {
      dA -= 360;
    } else if (dA < -180) {
      dA += 360;
    }

    int angle = safeAvoidance ? robotConfig.pathFindingAngleSafe() : robotConfig.pathFindingAngle();
    if (cmdRobot.getConsigne().getDistance() > 0) {
      return Math.abs(dA) <= angle;
    } else {
      return Math.abs(dA) >= 180 - angle;
    }
  }

}
