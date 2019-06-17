package org.arig.robot.config.spring;

import lombok.SneakyThrows;
import org.arig.robot.Ordonanceur;
import org.arig.robot.constants.IConstantesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NerellSimulator { //extends BootifullApplication {

    @SneakyThrows
    public static void main(final String [] args) {
        //boot(args);

        System.setProperty(IConstantesConfig.keyExecutionId, "0");
        SpringApplication.run(NerellSimulator.class, args);

        Ordonanceur.getInstance().run();
    }
}
