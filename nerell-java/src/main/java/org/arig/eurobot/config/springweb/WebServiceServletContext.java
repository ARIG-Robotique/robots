package org.arig.eurobot.config.springweb;

import org.arig.eurobot.constants.IConstantesSpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author gdepuille on 12/01/15.
 */
@Configuration
@Profile(IConstantesSpringConfig.profileMonitoring)
@EnableWebMvc
@ComponentScan({"org.arig.eurobot.web"})
public class WebServiceServletContext extends WebMvcConfigurerAdapter {}
