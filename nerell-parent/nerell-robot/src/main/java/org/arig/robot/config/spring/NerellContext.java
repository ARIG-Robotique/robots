package org.arig.robot.config.spring;

import org.arig.robot.model.RobotName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 30/10/16.
 */
@Configuration
public class NerellContext {

    @Bean
    public RobotName robotName() {
        return new RobotName().name("Nerell (The Big One)").version("1.1.0-SNAPSHOT (post robomovies)");
    }
}
