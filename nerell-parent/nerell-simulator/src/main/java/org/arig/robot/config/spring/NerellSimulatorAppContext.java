package org.arig.robot.config.spring;

import org.arig.robot.model.RobotName;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringInfluxDBWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 30/10/16.
 */
@Configuration
public class NerellSimulatorAppContext {

    @Bean
    public RobotName robotName() {
        return new RobotName().name("Nerell (simulator)").version("latest");
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }
}
