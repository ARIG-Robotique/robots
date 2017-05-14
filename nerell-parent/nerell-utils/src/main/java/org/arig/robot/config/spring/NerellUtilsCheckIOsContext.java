package org.arig.robot.config.spring;

import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.services.IOService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author gdepuille on 11/04/17.
 */
@Configuration
@Import({ NerellI2CContext.class, NerellAvoidingContext.class })
public class NerellUtilsCheckIOsContext {

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }

    @Bean
    public IOService ioService() {
        return new IOService();
    }
}
