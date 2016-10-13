package org.arig.test.robot.filters.pid;

import org.arig.robot.filters.pid.CompletePidFilter;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.InfluxDbWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 11/10/16.
 */
@Configuration
public class PidContext {

    @Bean
    public SimplePidFilter simplePID() {
        SimplePidFilter pid = new SimplePidFilter("simple_pid_test");
        pid.setTunings(1, 0, 0);

        return pid;
    }

    @Bean
    public CompletePidFilter completePID() {
        CompletePidFilter pid = new CompletePidFilter("complete_pid_test");
        pid.setControllerDirection(IPidFilter.PidType.DIRECT);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        pid.setSampleTime(1);
        pid.reset();
        pid.initialise();

        return pid;
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        InfluxDbWrapper w = new InfluxDbWrapper();
        w.setUrl("http://localhost:8086");
        w.setUsername("xx");
        w.setPassword("xx");
        w.setDbName("tua");
        w.setRetentionPolicy("autogen");

        return w;
    }
}
