package org.arig.robot.integration.spring.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by mythril on 20/12/13.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.arig.prehistobot")
@PropertySource(ignoreResourceNotFound = true, value = {
    "${config.data.directory}/prehistobot.properties"
})
public class ApplicationContext {


}

