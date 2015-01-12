package org.arig.prehistobot;

import lombok.extern.slf4j.Slf4j;
import org.arig.prehistobot.config.servlet3.MainRobotWebApplicationInitializer;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.util.Assert;
import org.springframework.web.WebApplicationInitializer;

/**
 * Created by gdepuille on 12/01/15.
 */
@Slf4j
public class JettyEmbeddedRunner {

    private Server srv;

    public void config() throws Exception {
        log.info("Configuration du Jetty embedded ...");

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

        // Application webapp
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

        final WebAppContext webAppCtx = new WebAppContext();
        webAppCtx.setConfigurations(new Configuration[] { myConfig });
        webAppCtx.setContextPath("/");
        webAppCtx.setParentLoaderPriority(true);

        srv.setHandler(webAppCtx);

        // Start Jetty
        srv.start();
    }

    public void join() throws Exception {
        Assert.notNull(srv, "La configuration du serveur doit être réalisé avant.");

        srv.join();
    }
}
