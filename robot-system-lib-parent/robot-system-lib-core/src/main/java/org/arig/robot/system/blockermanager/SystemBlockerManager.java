package org.arig.robot.system.blockermanager;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeConsigne;
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

    @Autowired
    private CommandeRobot cmdRobot;

    private final double seuilErreurPidDistance;
    private final double seuilErreurPidOrientation;

    public SystemBlockerManager(double seuilErreurPidDistance, double seuilErreurPidOrientation) {
        this.seuilErreurPidDistance = seuilErreurPidDistance;
        this.seuilErreurPidOrientation = seuilErreurPidOrientation;
    }


    @Override
    public void process() {

        double errorSumPidDistance = pidDistance.getPidErrorSum();
        double errorSumPidOrientation = pidOrientation.getPidErrorSum();

        if (errorSumPidDistance >= seuilErreurPidDistance || errorSumPidOrientation >= seuilErreurPidOrientation) {

            log.warn("L'erreur de pidDistance {} ou pidOrientation {} détectée ", errorSumPidDistance, errorSumPidOrientation);

            cmdRobot.getConsigne().setDistance(0);
            cmdRobot.getConsigne().setOrientation(0);
            cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);

            trajectoryManager.cancelMouvement();
        }
    }
}