package org.arig.robot.config.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"org.arig.robot.services"})
public class OdinCommonServicesContext {
}
