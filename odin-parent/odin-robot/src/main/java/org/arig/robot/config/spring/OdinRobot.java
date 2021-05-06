package org.arig.robot.config.spring;

import org.arig.robot.OdinOrdonanceur;
import org.arig.robot.constants.IConstantesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class OdinRobot {

    public static void main(final String[] args) throws IOException {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(IConstantesConfig.executiondIdFormat));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);

        ConfigurableApplicationContext context = SpringApplication.run(OdinRobot.class, args);
        context.getBean(OdinOrdonanceur.class).run();
        SpringApplication.exit(context);
        context.close();
        System.exit(0);
    }
}
