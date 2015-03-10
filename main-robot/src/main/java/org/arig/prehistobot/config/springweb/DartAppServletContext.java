package org.arig.prehistobot.config.springweb;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by gdepuille on 12/01/15.
 */
@Configuration
public class DartAppServletContext extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ajout du mapping pour servir l'application Dart
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }
}
