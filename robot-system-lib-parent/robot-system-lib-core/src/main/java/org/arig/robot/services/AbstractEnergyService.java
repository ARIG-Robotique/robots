package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractEnergyService {

    @Autowired
    private MonitoringWrapper monitoringWrapper;

    @Autowired
    private RobotConfig robotConfig;

    public abstract double tensionServos();
    public abstract double courantServos();

    public abstract double tensionMoteurs();
    public abstract double courantMoteurs();

    public boolean checkServos() {
        return checkServos(true);
    }

    public boolean checkServos(boolean withLog) {
        final double tension = tensionServos();
        final double courant = courantServos();

        if (withLog) {
            log.info("Energy servos : {} V ; {} A ; seuil tension {} V", tension, courant, robotConfig.seuilTensionServos());
        }

        // Construction du monitoring
        final MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("power")
                .addField("servosTension", tension)
                .addField("servosCourant", courant);
        monitoringWrapper.addTimeSeriePoint(serie);

        return tension > robotConfig.seuilTensionServos();
    }

    public boolean checkMoteurs() {
        return checkMoteurs(true);
    }

    public boolean checkMoteurs(boolean withLog) {
        final double tension = tensionMoteurs();
        final double courant = courantMoteurs();

        if (withLog) {
            log.info("Energy moteurs : {} V ; {} A ; seuil tension {} V", tension, courant, robotConfig.seuilTensionMoteurs());
        }

        // Construction du monitoring
        final MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("power")
                .addField("moteursTension", tension)
                .addField("moteursCourant", courant);
        monitoringWrapper.addTimeSeriePoint(serie);

        return tension > robotConfig.seuilTensionMoteurs();
    }
}
