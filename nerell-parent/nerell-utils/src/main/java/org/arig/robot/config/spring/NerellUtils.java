package org.arig.robot.config.spring;

import org.arig.robot.constants.IConstantesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class NerellUtils {

    public static void main(String ... args) {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);

        SpringApplication.run(NerellUtils.class, args);
    }
}
