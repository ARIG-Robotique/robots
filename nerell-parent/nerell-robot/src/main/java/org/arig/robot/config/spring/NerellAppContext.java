package org.arig.robot.config.spring;

import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.RobotName;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringInfluxDBWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author gdepuille on 30/10/16.
 */
@Configuration
public class NerellAppContext {

    @Autowired
    private Environment env;

    @Bean
    public RobotName robotName() {
        return new RobotName().name("Nerell (The Big One)").version("latest");
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        if (env.acceptsProfiles(IConstantesConfig.profileMonitoring)) {
            final MonitoringInfluxDBWrapper w = new MonitoringInfluxDBWrapper();
            w.setUrl("http://sglk-dxf5xy1-lnx:8086");
            w.setUsername("root");
            w.setPassword("root");
            w.setDbName("nerell");
            w.setRetentionPolicy("autogen");

            return w;

        } else {
            return new MonitoringJsonWrapper();
        }
    }
}
