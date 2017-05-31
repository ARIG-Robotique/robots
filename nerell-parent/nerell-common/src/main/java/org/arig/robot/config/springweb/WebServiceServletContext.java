package org.arig.robot.config.springweb;

import org.arig.robot.constants.IConstantesConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author gdepuille on 12/01/15.
 */
@Configuration
@Profile(IConstantesConfig.profileMonitoring)
@EnableWebMvc
@ComponentScan({"org.arig.robot.web"})
public class WebServiceServletContext extends WebMvcConfigurerAdapter {}
