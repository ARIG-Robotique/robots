package org.arig.robot.config.spring;

import org.arig.robot.constants.ConstantesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class PamiUtils {

    public static void main(String ... args) throws IOException {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat));
        System.setProperty(ConstantesConfig.keyExecutionId, execId);

        String pamiId = InetAddress.getLocalHost().getHostName().replace("pami-", "");
        System.setProperty(ConstantesConfig.keyPamiId, pamiId);

        String springProfiles = System.getProperty(ConstantesConfig.keySpringProfiles);
        System.setProperty(ConstantesConfig.keySpringProfiles, springProfiles + "," + pamiId);

        // FIXME : Workaround
        System.setProperty("spring.main.allow-circular-references", "true");

        SpringApplication.run(PamiUtils.class, args);
    }
}
