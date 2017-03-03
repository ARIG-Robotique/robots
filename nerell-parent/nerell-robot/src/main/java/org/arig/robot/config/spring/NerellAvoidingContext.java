package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.services.avoiding.BasicAvoidingService;
import org.arig.robot.services.avoiding.CompleteAvoidingService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author gdepuille on 23/12/14.
 */
@Slf4j
@Configuration
public class NerellAvoidingContext {

    @Autowired
    private Environment env;

    @Bean
    public IAvoidingService avoidingService() {
        IConstantesNerellConfig.AvoidingSelection avoidingImplementation = env.getProperty("avoidance.service.implementation", IConstantesNerellConfig.AvoidingSelection.class);
        if (avoidingImplementation == IConstantesNerellConfig.AvoidingSelection.BASIC) {
            return new BasicAvoidingService();
        } else {
            return new CompleteAvoidingService();
        }
    }
}
