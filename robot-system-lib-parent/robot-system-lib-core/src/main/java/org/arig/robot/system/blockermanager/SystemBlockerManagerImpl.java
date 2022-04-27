package org.arig.robot.system.blockermanager;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SystemBlockerManagerImpl implements SystemBlockerManager {

    private static final byte MAX_ERROR_DISTANCE = 3;
    private static final byte MAX_ERROR_ORIENTATION = 3;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private TrajectoryManager trajectoryManager;

    @Autowired
    private Abstract2WheelsEncoders encoders;

    @Autowired
    protected MonitoringWrapper monitoringWrapper;

    private final double seuilDistancePulse;
    private final double seuilOrientationPulse;

    private byte countErrorDistance = 0;
    private byte countErrorOrientation = 0;

    public SystemBlockerManagerImpl(double seuilDistancePulse, double seuilOrientationPulse) {
        this.seuilDistancePulse = seuilDistancePulse;
        this.seuilOrientationPulse = seuilOrientationPulse;
    }

    @Override
    public void reset() {
        countErrorDistance = 0;
        countErrorOrientation = 0;
    }

    @Override
    public void process() {
        if (cmdRobot.isType(TypeConsigne.DIST, TypeConsigne.XY) && !trajectoryManager.isTrajetAtteint() &&
                Math.abs(encoders.getDistance()) < seuilDistancePulse) {
            countErrorDistance++;

        } else {
            countErrorDistance = 0;
        }

        if (cmdRobot.isType(TypeConsigne.ANGLE, TypeConsigne.XY)  && !trajectoryManager.isTrajetAtteint() &&
                Math.abs(encoders.getOrientation()) < seuilOrientationPulse) {
            countErrorOrientation++;

        } else {
            countErrorOrientation = 0;
        }

        // Construction du monitoring
        final MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("blocker")
                .addField("maxErrorDistance", MAX_ERROR_DISTANCE)
                .addField("maxErrorOrientation", MAX_ERROR_ORIENTATION)
                .addField("seuilDistance", seuilDistancePulse)
                .addField("seuilOrientation", seuilOrientationPulse)
                .addField("countErrorDistance", countErrorDistance)
                .addField("countErrorOrientation", countErrorOrientation);

        monitoringWrapper.addTimeSeriePoint(serie);

        // x itérations de 500 ms (cf Scheduler)
        if (countErrorDistance >= MAX_ERROR_DISTANCE && countErrorOrientation >= MAX_ERROR_ORIENTATION) {
            log.warn("Détection de blocage trop importante : distance {} ; orientation {}", countErrorDistance, countErrorOrientation);

            trajectoryManager.cancelMouvement();
            reset();
        }
    }
}
