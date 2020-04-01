package org.arig.robot.config.spring;

import lombok.SneakyThrows;
import org.arig.robot.Ordonanceur;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.model.RobotStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class NerellSimulator {

    @SneakyThrows
    public static void main(final String[] args) {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);

        ConfigurableApplicationContext context = SpringApplication.run(NerellSimulator.class, args);
        RobotStatus rs = context.getBean(RobotStatus.class);
        rs.setSimulateur();

        IPidFilter pidDistance = context.getBean("pidDistance", IPidFilter.class);
        pidDistance.setTunings(IConstantesNerellConfig.kpDistanceSimu, IConstantesNerellConfig.kiDistanceSimu, IConstantesNerellConfig.kdDistanceSimu);

        IPidFilter pidOrientation = context.getBean("pidOrientation", IPidFilter.class);
        pidOrientation.setTunings(IConstantesNerellConfig.kpOrientationSimu, IConstantesNerellConfig.kiOrientationSimu, IConstantesNerellConfig.kdOrientationSimu);

        Ordonanceur.getInstance().run();
        context.close();
        System.exit(0);
    }
}
