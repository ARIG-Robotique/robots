package org.arig.robot.config.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 23/04/15.
 */
@Configuration
@ComponentScan({"org.arig.robot.services"})
public class NerellServicesContext { }
