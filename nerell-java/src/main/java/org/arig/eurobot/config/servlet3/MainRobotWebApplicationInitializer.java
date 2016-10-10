package org.arig.eurobot.config.servlet3;

import org.arig.eurobot.config.springweb.FrontAppServletContext;
import org.arig.eurobot.config.springweb.WebServiceServletContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Created by gdepuille on 12/01/15.
 */
public class MainRobotWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 1. Initialisation du context Root de spring
        final AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.scan("org.arig.eurobot.config.spring");

        // 2. Ajout des listeners
        servletContext.addListener(new ContextLoaderListener(rootContext));
        servletContext.addListener(new RequestContextListener());

        // 3. Ajout des filtres
        final CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        FilterRegistration fr = servletContext.addFilter("CharacterEncodingFilter", filter);
        fr.addMappingForUrlPatterns(null, false, "/*");

        // 4. Ajout des servlets des applications
        final AnnotationConfigWebApplicationContext webServletContext = new AnnotationConfigWebApplicationContext();
        webServletContext.register(WebServiceServletContext.class);
        ServletRegistration.Dynamic sr = servletContext.addServlet("ws", new DispatcherServlet(webServletContext));
        sr.addMapping("/ws/*");
        sr.setLoadOnStartup(1);

        final AnnotationConfigWebApplicationContext appServletContext = new AnnotationConfigWebApplicationContext();
        appServletContext.register(FrontAppServletContext.class);
        sr = servletContext.addServlet("app", new DispatcherServlet(webServletContext));
        sr.addMapping("/app/*");
        sr.setLoadOnStartup(2);
    }
}
