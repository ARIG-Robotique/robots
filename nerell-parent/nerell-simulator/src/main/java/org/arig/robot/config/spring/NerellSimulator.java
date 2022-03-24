package org.arig.robot.config.spring;

import lombok.SneakyThrows;
import org.arig.robot.NerellOrdonanceur;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.ramp.GainFactorRampFilter;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.system.encoders.BouchonARIG2WheelsEncoders;
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
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat));
        System.setProperty(ConstantesConfig.keyExecutionId, execId);

        ConfigurableApplicationContext context = SpringApplication.run(NerellSimulator.class, args);
        NerellRobotStatus rs = context.getBean(NerellRobotStatus.class);
        rs.simulateur(true);

        GainFactorRampFilter rampDistance = context.getBean("rampDistance", GainFactorRampFilter.class);
        rampDistance.setGain(NerellConstantesConfig.gainVitesseRampeDistanceSimulateur);

        GainFactorRampFilter rampOrientation = context.getBean("rampOrientation", GainFactorRampFilter.class);
        rampOrientation.setGain(NerellConstantesConfig.gainVitesseRampeOrientationSimulateur);

        PidFilter pidDistance = context.getBean("pidDistance", PidFilter.class);
        pidDistance.setTunings(NerellConstantesConfig.kpDistanceSimu, NerellConstantesConfig.kiDistanceSimu, NerellConstantesConfig.kdDistanceSimu);

        PidFilter pidOrientation = context.getBean("pidOrientation", PidFilter.class);
        pidOrientation.setTunings(NerellConstantesConfig.kpOrientationSimu, NerellConstantesConfig.kiOrientationSimu, NerellConstantesConfig.kdOrientationSimu);

        // Affichage des bornes pour le limiteur des moteurs
        context.getBean(BouchonARIG2WheelsEncoders.class).printLimiterValues();

        // Démarrage de l'ordonancement de match
        context.getBean(NerellOrdonanceur.class).run();
        context.close();
        System.exit(0);
    }
}
