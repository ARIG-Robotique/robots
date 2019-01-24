package org.arig.robot.config.spring;

import org.arig.robot.constants.IConstantesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class SimulatorNerell {

    public static void main(final String [] args) {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);

        // Initialisation du logger après définition de la property pour prise en compte
        // lors de la création du fichier de log.
        final Logger log = LoggerFactory.getLogger(SimulatorNerell.class);
        log.info("Demarrage de Nerell {} ...", execId);

        // Demmarage de SpringBoot
        SpringApplication.run(SimulatorNerell.class, args);
    }

    public void onExit() throws IOException {
        // Ecriture d'un fichier identifiant une execution termine.
        final String execId = System.getProperty(IConstantesConfig.keyExecutionId);
        final File execFile = new File("./logs/" + execId + ".exec");
        final Logger log = LoggerFactory.getLogger(SimulatorNerell.class);
        log.info("Création du fichier de fin d'éxécution {} : {}", execFile.getAbsolutePath(), execFile.createNewFile());
    }
}
