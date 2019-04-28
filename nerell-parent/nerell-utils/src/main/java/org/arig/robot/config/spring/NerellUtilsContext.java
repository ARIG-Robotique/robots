package org.arig.robot.config.spring;

import lombok.SneakyThrows;
import org.arig.robot.model.RobotName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.arig.robot.nerell.utils")
public class NerellUtilsContext extends NerellRobotContext {

    @Bean
    @Override
    @SneakyThrows
    public RobotName robotName() {
        final RobotName rn = super.robotName();
        rn.name("Nerell Shell");

        return rn;
    }
}
