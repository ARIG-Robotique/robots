package org.arig.robot.config.spring;

import org.arig.robot.model.RobotName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.arig.robot.nerell.utils")
public class NerellUtilsContext {

    @Bean
    public RobotName robotName() {
        return new RobotName().name("Nerell Utils").version("latest");
    }
}
