package org.arig.robot.config.spring;

import org.arig.robot.model.RobotName;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author gdepuille on 30/10/16.
 */
@Configuration
public class NerellSimulatorAppContext {

    @Autowired
    private Environment env;

    @Bean
    public RobotName robotName() {
        return new RobotName().name("Nerell (simulator)").version("latest");
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("monitoring.points.enable", Boolean.class, true));
        return mjw;
    }
}
