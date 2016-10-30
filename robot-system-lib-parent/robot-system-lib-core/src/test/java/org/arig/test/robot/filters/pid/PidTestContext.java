package org.arig.test.robot.filters.pid;

import org.arig.robot.filters.pid.CompletePidFilter;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;

/**
 * @author gdepuille on 11/10/16.
 */
@Configuration
public class PidTestContext {

    @Autowired
    private Environment env;

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
        String directory = String.format("%s%s%s", env.getRequiredProperty("java.io.tmpdir"), File.separator, "arig/robot/pidTest");
        return new MonitoringJsonWrapper(directory);
    }
}
