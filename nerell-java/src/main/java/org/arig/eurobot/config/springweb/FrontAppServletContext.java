package org.arig.eurobot.config.springweb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by gdepuille on 12/01/15.
 */
@Configuration
public class FrontAppServletContext extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ajout du mapping pour servir l'application Front
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }
}
