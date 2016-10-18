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
public class NerellContext {

    @Bean
    public RobotName robotName() {
        return new RobotName().name("Nerell (simulator)").version("latest");
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        MonitoringInfluxDBWrapper w = new MonitoringInfluxDBWrapper();
        w.setUrl("http://sglk-dxf5xy1-lnx:8086");
        w.setUsername("root");
        w.setPassword("root");
        w.setDbName("nerell_simulator");
        w.setRetentionPolicy("autogen");

        return w;
    }
}
