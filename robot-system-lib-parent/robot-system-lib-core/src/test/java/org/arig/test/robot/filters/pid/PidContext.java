package org.arig.test.robot.filters.pid;

import org.arig.robot.filters.pid.CompletePID;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePID;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.InfluxDbWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gregorydepuille@sglk.local on 11/10/16.
 */
@Configuration
public class PidContext {

    @Bean
    public SimplePID simplePID() {
        SimplePID pid = new SimplePID("simple_pid_test");
        pid.setTunings(1, 0, 0);

        return pid;
    }

    @Bean
    public CompletePID completePID() {
        CompletePID pid = new CompletePID("complete_pid_test");
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
