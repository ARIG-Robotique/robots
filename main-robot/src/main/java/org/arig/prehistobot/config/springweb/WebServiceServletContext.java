package org.arig.prehistobot.config.springweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by gdepuille on 12/01/15.
 */
@Configuration
@EnableWebMvc
@ComponentScan({"org.arig.prehistobot.web"})
public class WebServiceServletContext extends WebMvcConfigurerAdapter {
}
