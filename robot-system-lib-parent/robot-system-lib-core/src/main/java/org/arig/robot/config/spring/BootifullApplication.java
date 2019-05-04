package org.arig.robot.config.spring;

import org.arig.robot.constants.IConstantesConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootifullApplication {

    protected static void boot(String ... args) {
        // Initialisation avec 0 pour disposer d'un fichier tous de même
        System.setProperty(IConstantesConfig.keyExecutionId, "0");

        //Application.launch(JavaFXApplication.class, args);
    }
}
