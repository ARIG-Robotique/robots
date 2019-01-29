package org.arig.robot.config.spring;

import javafx.application.Application;
import org.arig.robot.ui.JavaFXApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootifullApplication {

    protected static void boot(String ... args) {
        Application.launch(JavaFXApplication.class, args);
    }
}
