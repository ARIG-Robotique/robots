package org.arig.prehistobot.config.servlet3;

import org.arig.prehistobot.config.springweb.WebServletContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Created by gdepuille on 12/01/15.
 */
public class MainRobotWebApplicationInitializer implements WebApplicationInitializer {

    /** Index pour le chargement des servlets */
    private static int loadIndex = 0;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 1. Initialisation du context Root de spring
        final AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.scan("org.arig.prehistobot.config.spring");

        // 2. Ajout des listeners
        servletContext.addListener(new ContextLoaderListener(rootContext));
        //servletContext.addListener(new MetricsServletsSpringContextListener());

        // 3. Ajout des filtres
        final CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        FilterRegistration fr = servletContext.addFilter("CharacterEncodingFilter", filter);
        fr.addMappingForUrlPatterns(null, false, "/*");

        final RequestContextFilter requestContextFilter = new RequestContextFilter();
        fr = servletContext.addFilter("requestContextFilter", requestContextFilter);
        fr.addMappingForUrlPatterns(null, true, "/*");

        //fr = servletContext.addFilter("MetricsIntrumentedFilter", new ExcludeMonitoringIntrumentedFilter());
        //fr.addMappingForUrlPatterns(null, false, "/*");

        // 4. Ajout des servlets des applications
        final AnnotationConfigWebApplicationContext webServletContext = new AnnotationConfigWebApplicationContext();
        webServletContext.register(WebServletContext.class);
        ServletRegistration.Dynamic sr = servletContext.addServlet("web", new DispatcherServlet(webServletContext));
        sr.addMapping("/ws/*");
        sr.setLoadOnStartup(1);

        //sr = servletContext.addServlet("metricsAdminServlet ", AdminServlet.class);
        //sr.addMapping("/monitoring/*");
        //sr.setLoadOnStartup(2);
    }
}
