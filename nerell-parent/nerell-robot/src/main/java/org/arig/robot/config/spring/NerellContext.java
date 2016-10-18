package org.arig.robot.config.spring;

import org.arig.robot.model.RobotName;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 30/10/16.
 */
@Configuration
public class NerellContext {

    @Bean
    public RobotName robotName() {
        return new RobotName().name("Nerell (The Big One)").version("latest");
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper("logs/timeDatas");
    }
}
