package org.arig.robot.filters.pid;

import org.arig.robot.model.MonitorPoint;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

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
        MonitorPoint serie = new MonitorPoint()
                .tableName(name)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("setPoint", getSetPoint())
                .addField("input", getInput())
                .addField("error", getError())
                .addField("errorSum", getErrorSum())
                .addField("output", getOutput());

        monitoringWrapper.addPoint(serie);
    }
}
