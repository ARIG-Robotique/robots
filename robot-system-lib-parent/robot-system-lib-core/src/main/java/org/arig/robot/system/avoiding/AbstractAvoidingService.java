package org.arig.robot.system.avoiding;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.services.LidarService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
public abstract class AbstractAvoidingService implements IAvoidingService {

    @Autowired
    protected TrajectoryManager trajectoryManager;

    @Autowired
    protected LidarService lidarService;

    @Autowired
    protected ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    protected Position currentPosition;

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
        return lidarService.getDetectedPointsMm().parallelStream()
                .anyMatch(pt -> checkValidPointForSeuil(pt, seuil));
    }

    protected boolean needLowSpeed() {
        return lidarService.getDetectedPointsMm().parallelStream()
                .anyMatch(pt -> checkValidPointForSeuil(pt, robotConfig.pathFindingSeuilProximite() * 2));
    }

    private boolean checkValidPointForSeuil(Point pt, int seuilMm) {
        long dX = (long) (pt.getX() - conv.pulseToMm(currentPosition.getPt().getX()));
        long dY = (long) (pt.getY() - conv.pulseToMm(currentPosition.getPt().getY()));
        double distanceMm = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

        if (distanceMm > seuilMm) {
            return false;
        }

        double alpha = Math.toDegrees(Math.atan2(dY, dX));
        double dA = alpha - conv.pulseToDeg(currentPosition.getAngle());
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
