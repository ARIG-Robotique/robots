package org.arig.prehistobot;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by mythril on 20/12/13.
 */
@Slf4j
public class RobotApplication {

    public static void main(final String [] args) throws Exception {
        log.info("Demarrage du robot principal ...");

        // Configuration de Jetty
        JettyEmbeddedRunner jetty = new JettyEmbeddedRunner();
        jetty.config();
        log.info("Robot principal configuré.");

        log.info("Demarrage de l'ordonancement");
        Ordonanceur.getInstance().run();

        log.info("Fin du programme");
        jetty.stop();
    }
}
