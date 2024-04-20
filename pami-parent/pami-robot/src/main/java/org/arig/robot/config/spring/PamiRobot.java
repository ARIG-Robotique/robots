package org.arig.robot.config.spring;

import org.arig.robot.PamiOrdonanceur;
import org.arig.robot.constants.ConstantesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class PamiRobot {

    public static void main(final String[] args) throws IOException {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat));
        System.setProperty(ConstantesConfig.keyExecutionId, execId);

        String pamiId = InetAddress.getLocalHost().getHostName().replace("pami-", "");
        System.setProperty(ConstantesConfig.keyPamiId, pamiId);

        ConfigurableApplicationContext context = SpringApplication.run(PamiRobot.class, args);
        context.getBean(PamiOrdonanceur.class).run();
        SpringApplication.exit(context);
        context.close();
        System.exit(0);
    }
}
