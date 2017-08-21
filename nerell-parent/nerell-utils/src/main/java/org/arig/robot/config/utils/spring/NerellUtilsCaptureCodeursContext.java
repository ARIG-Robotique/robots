package org.arig.robot.config.utils.spring;

import org.arig.robot.config.spring.NerellRobotI2CContext;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author gdepuille on 02/11/16.
 */
@Configuration
@Import({ NerellRobotI2CContext.class })
public class NerellUtilsCaptureCodeursContext {

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }
}
