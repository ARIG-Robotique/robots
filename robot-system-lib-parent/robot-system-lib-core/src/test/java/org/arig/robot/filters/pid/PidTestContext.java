package org.arig.robot.filters.pid;

import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author gdepuille on 11/10/16.
 */
@Configuration
public class PidTestContext {

    @Bean
    @Primary
    public SimplePidFilter simplePID() {
        SimplePidFilter pid = new SimplePidFilter("test");
        pid.setTunings(1, 0, 0);

        return pid;
    }

    @Bean
    public SimplePidFilter simplePIDIntegralLimit() {
        SimplePidFilter pid = new SimplePidFilter("test", true);
        pid.setTunings(1, 0, 0);

        return pid;
    }

    @Bean
    public DerivateInputPidFilter derivateInputPID() {
        DerivateInputPidFilter pid = new DerivateInputPidFilter("test");
        pid.setTunings(1, 0, 0);

        return pid;
    }

    @Bean
    public MonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }
}
