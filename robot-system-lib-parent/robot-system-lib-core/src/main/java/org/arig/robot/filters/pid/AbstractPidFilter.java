package org.arig.robot.filters.pid;

import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gdepuille on 12/10/16.
 */
public abstract class AbstractPidFilter implements IPidFilter {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    private final String name;

    protected AbstractPidFilter(final String name) {
        this.name = name;
    }

    protected void sendMonitoring() {
        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("correcteur_pid")
                .addTag(MonitorTimeSerie.TAG_NAME, name)
                .addTag("implementation", pidImpl())
                .addField("kp", getKp())
                .addField("ki", getKi())
                .addField("kd", getKd())
                .addField("consigne", getConsigne())
                .addField("mesure", getMesure())
                .addField("error", getError())
                .addField("errorSum", getErrorSum())
                .addField("output", getOutput());

        monitoringWrapper.addTimeSeriePoint(serie);
    }

    protected abstract String pidImpl();
}
