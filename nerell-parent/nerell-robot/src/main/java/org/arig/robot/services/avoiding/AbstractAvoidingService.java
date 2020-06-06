package org.arig.robot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.system.ILidarService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
public abstract class AbstractAvoidingService implements IAvoidingService {

    @Autowired
    protected ITrajectoryManager trajectoryManager;

    @Autowired
    protected ILidarService lidarService;

    @Autowired
    protected ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    protected Position currentPosition;

    @Autowired
    protected CommandeRobot cmdRobot;

    @Autowired
    protected NerellRobotStatus rs;

    protected abstract void processAvoiding();

    public final void process() {
        lidarService.refreshDetectedPoints();

        if (lidarService.mustCleanup()) {
            lidarService.refreshObstacles();
        }

        processAvoiding();
    }

    protected boolean hasProximite() {
        return lidarService.getDetectedPointsMm().parallelStream()
                .anyMatch(pt -> checkValidPointForSeuil(pt, IConstantesNerellConfig.pathFindingSeuilProximite));
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

        if (cmdRobot.getConsigne().getDistance() > 0) {
            return Math.abs(dA) <= IConstantesNerellConfig.pathFindingAngle;
        } else {
            return Math.abs(dA) >= 180 - IConstantesNerellConfig.pathFindingAngle;
        }
    }

}
