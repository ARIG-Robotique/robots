package org.arig.robot;

import org.arig.robot.constants.IConstantesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author gdepuille on 20/12/13.
 */
public class RobotApplication {

    public static void main(final String [] args) throws Exception {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);

        // Initialisation du logger après définition de la property pour prise en compte
        // lors de la création du fichier de log.
        final Logger log = LoggerFactory.getLogger(RobotApplication.class);
        log.info("Demarrage de Nerell {} ...", execId);

        // Configuration de Jetty
        JettyEmbeddedRunner jetty = new JettyEmbeddedRunner();
        jetty.config();
        log.info("Robot principal configuré.");

        log.info("Demarrage de l'ordonancement");
        Ordonanceur.getInstance().run();

        log.info("Fin du programme");
        jetty.stop();

        // Ecriture d'un fichier identifiant une execution termine.
        final File execFile = new File("./logs/" + execId + ".exec");
        log.info("Création du fichier de fin d'éxécution {} : {}", execFile.getAbsolutePath(), execFile.createNewFile());

        System.exit(0);
    }
}
