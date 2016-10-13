package org.arig.robot.filters.pid;

import org.arig.robot.monitoring.IMonitoringWrapper;
import org.influxdb.dto.Point;
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
        Point serie = Point.measurement(name)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("setPoint", getSetPoint())
                .addField("input", getInput())
                .addField("error", getError())
                .addField("errorSum", getErrorSum())
                .addField("output", getOutput())
                .build();

        monitoringWrapper.write(serie);
    }
}
