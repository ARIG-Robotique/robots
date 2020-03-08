package org.arig.robot.system.blockermanager;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.model.monitor.MonitorMouvementPath;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
public class SystemBlockerManager implements ISystemBlockerManager {

    @Autowired
    @Qualifier("pidDistance")
    private IPidFilter pidDistance;

    @Autowired
    @Qualifier("pidOrientation")
    private IPidFilter pidOrientation;

    @Autowired
    private ITrajectoryManager trajectoryManager;

    private final double seuilErreurPidDistance;
    private final double seuilErreurPidOrientation;

    public SystemBlockerManager(double seuilErreurPidDistance, double seuilErreurPidOrientation) {
        this.seuilErreurPidDistance = seuilErreurPidDistance;
        this.seuilErreurPidOrientation = seuilErreurPidOrientation;
    }


    @Override
    public void process() {

        double errorSumPidDistance = Math.abs(pidDistance.getPidErrorSum());
        double errorSumPidOrientation = Math.abs(pidOrientation.getPidErrorSum());

        if (trajectoryManager.getCurrentMouvement() != null && trajectoryManager.getCurrentMouvement() instanceof MonitorMouvementPath
                && (errorSumPidDistance >= seuilErreurPidDistance || errorSumPidOrientation >= seuilErreurPidOrientation)) {

            log.warn("L'erreur de pidDistance {} ou pidOrientation {} détectée ", errorSumPidDistance, errorSumPidOrientation);

            trajectoryManager.cancelMouvement();
        }
    }
}
