package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.config.servlet3.MainRobotWebApplicationInitializer;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.util.Assert;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.DispatcherType;

/**
 * @author gdepuille on 12/01/15.
 */
@Slf4j
public class JettyEmbeddedRunner {

    private Server srv;

    public void config() throws Exception {
        log.info("Configuration de Jetty ...");

        final int port = 8080;

        // Pool de thread
        final QueuedThreadPool tp = new QueuedThreadPool(10, 2);

        // Server
        srv = new Server(tp);
        srv.setDumpAfterStart(false);
        srv.setDumpBeforeStop(false);
        srv.setStopAtShutdown(true);

        // Scheduler
        srv.addBean(new ScheduledExecutorScheduler());

        // Connecteur HTTP
        final HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setOutputBufferSize(32768);
        httpConfig.setRequestHeaderSize(8192);
        httpConfig.setResponseHeaderSize(8192);
        httpConfig.setSendDateHeader(false);
        httpConfig.setSendServerVersion(true);
        httpConfig.setSendXPoweredBy(true);

        final ServerConnector httpConnector = new ServerConnector(srv, new HttpConnectionFactory(httpConfig));
        httpConnector.setPort(port);
        httpConnector.setIdleTimeout(30000);
        srv.addConnector(httpConnector);

        // Configuration du bootstrap de l'application servlet 3.x
        final AnnotationConfiguration myConfig = new AnnotationConfiguration() {

            @Override
            public void preConfigure(final WebAppContext context) throws Exception {
                super.preConfigure(context);

                // Ajout de notre classe de config remplacante du web.xml
                final ClassInheritanceMap map = new ClassInheritanceMap();
                final ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
                set.add(MainRobotWebApplicationInitializer.class.getName());
                map.put(WebApplicationInitializer.class.getName(),  set);
                context.setAttribute(AnnotationConfiguration.CLASS_INHERITANCE_MAP, map);
                _classInheritanceHandler = new ClassInheritanceHandler(map);
            }
        };

        FilterHolder corsFilterHolder = new FilterHolder(CrossOriginFilter.class);
        corsFilterHolder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        corsFilterHolder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        corsFilterHolder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        corsFilterHolder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        final WebAppContext webAppCtx = new WebAppContext();
        webAppCtx.setConfigurations(new Configuration[] { myConfig });
        webAppCtx.setContextPath("/");
        webAppCtx.setParentLoaderPriority(true);
        webAppCtx.addFilter(corsFilterHolder, "/", null);

        srv.setHandler(webAppCtx);

        // Start Jetty
        log.info("Demarrage de Jetty ...");
        srv.start();
    }

    public void join() throws Exception {
        Assert.notNull(srv, "La configuration du serveur doit être réalisé avant.");

        // Association au Pool de Threads
        log.info("Association au pool de thread principal");
        srv.join();
    }

    public void stop() throws Exception {
        Assert.notNull(srv, "La configuration du serveur doit être réalisé avant.");

        if (srv.isRunning()) {
            srv.stop();
        }
    }
}
