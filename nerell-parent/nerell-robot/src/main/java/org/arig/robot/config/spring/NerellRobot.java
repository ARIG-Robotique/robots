package org.arig.robot.config.spring;

import org.arig.robot.NerellOrdonanceur;
import org.arig.robot.constants.ConstantesConfig;
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
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat));
        System.setProperty(ConstantesConfig.keyExecutionId, execId);

        ConfigurableApplicationContext context = SpringApplication.run(NerellRobot.class, args);
        context.getBean(NerellOrdonanceur.class).run();
        SpringApplication.exit(context);
        context.close();
        System.exit(0);
    }
}
