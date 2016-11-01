package org.arig.robot.config.spring;

import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author gdepuille on 02/11/16.
 */
@Configuration
@Import({ I2CContext.class })
public class CaptureCodeursContext {

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper("/tmp");
    }
}
