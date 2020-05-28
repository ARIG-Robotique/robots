package org.arig.robot.system.blockermanager;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.common.DerivateFilter;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SystemBlockerManager implements ISystemBlockerManager {

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private Abstract2WheelsEncoders encoders;

    private final DerivateFilter derivateDistance = new DerivateFilter(0d);
    private final DerivateFilter derivateOrientation = new DerivateFilter(0d);

    private final double seuilDistancePulse;
    private final double seuilOrientationPulse;

    private byte countErrorDistance = 0;
    private byte countErrorOrientation = 0;

    public SystemBlockerManager(double seuilDistancePulse, double seuilOrientationPulse) {
        this.seuilDistancePulse = seuilDistancePulse;
        this.seuilOrientationPulse = seuilOrientationPulse;
    }

    @Override
    public void reset() {
        derivateDistance.reset();
        derivateOrientation.reset();
        countErrorDistance = 0;
        countErrorOrientation = 0;
    }

    @Override
    public void process() {
        if (!trajectoryManager.isTrajetAtteint() && derivateDistance.filter(encoders.getDistance()) < seuilDistancePulse) {
            countErrorDistance++;
        } else {
            derivateDistance.reset();
            countErrorDistance = 0;
        }

        if (!trajectoryManager.isTrajetAtteint() && derivateOrientation.filter(encoders.getOrientation()) < seuilOrientationPulse) {
            countErrorOrientation++;
        } else {
            derivateOrientation.reset();
            countErrorOrientation = 0;
        }

        // 10 itération de 500 ms (cf Scheduler)
        if (countErrorDistance >= 10 && countErrorOrientation >= 10) {
            log.warn("Détection de blocage trop importante : distance {} ; orientation {}", countErrorDistance, countErrorOrientation);

            trajectoryManager.cancelMouvement();
            reset();
        }
    }
}
