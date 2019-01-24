package org.arig.test.robot.filters.pid;

import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 11/10/16.
 */
@Configuration
public class PidTestContext {

    @Bean
    public SimplePidFilter simplePID() {
        SimplePidFilter pid = new SimplePidFilter("test");
        pid.setTunings(1, 0, 0);

        return pid;
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }
}
