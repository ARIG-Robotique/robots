package org.arig.robot.config.spring;

import org.arig.robot.Ordonanceur;
import org.arig.robot.constants.IConstantesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class NerellRobot {

    public static void main(final String[] args) throws IOException {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);

        ConfigurableApplicationContext context = SpringApplication.run(NerellRobot.class, args);
        Ordonanceur.getInstance().run();
        SpringApplication.exit(context);
        context.close();
    }
}
