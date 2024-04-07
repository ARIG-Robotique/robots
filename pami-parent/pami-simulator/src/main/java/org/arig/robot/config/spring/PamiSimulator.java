package org.arig.robot.config.spring;

import lombok.SneakyThrows;
import org.arig.robot.PamiOrdonanceur;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.constants.PamiConstantesConfig;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.ramp.GainFactorRampFilter;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.system.encoders.BouchonARIG2WheelsEncoders;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class PamiSimulator {

    @SneakyThrows
    public static void main(final String[] args) {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat));
        System.setProperty(ConstantesConfig.keyExecutionId, execId);

        // TODO : Get PAMI ID from configuration
        System.setProperty(ConstantesConfig.keyPamiId, "triangle");

        ConfigurableApplicationContext context = SpringApplication.run(PamiSimulator.class, args);
        PamiRobotStatus rs = context.getBean(PamiRobotStatus.class);
        rs.simulateur(true);

        GainFactorRampFilter rampDistance = context.getBean("rampDistance", GainFactorRampFilter.class);
        rampDistance.setGain(PamiConstantesConfig.gainVitesseRampeDistanceSimulateur);

        GainFactorRampFilter rampOrientation = context.getBean("rampOrientation", GainFactorRampFilter.class);
        rampOrientation.setGain(PamiConstantesConfig.gainVitesseRampeOrientationSimulateur);

        PidFilter pidDistance = context.getBean("pidDistance", PidFilter.class);
        pidDistance.setTunings(PamiConstantesConfig.kpDistanceSimu, PamiConstantesConfig.kiDistanceSimu, PamiConstantesConfig.kdDistanceSimu);

        PidFilter pidOrientation = context.getBean("pidOrientation", PidFilter.class);
        pidOrientation.setTunings(PamiConstantesConfig.kpOrientationSimu, PamiConstantesConfig.kiOrientationSimu, PamiConstantesConfig.kdOrientationSimu);

        // Affichage des bornes pour le limiteur des moteurs
        context.getBean(BouchonARIG2WheelsEncoders.class).printLimiterValues();

        // Démarrage de l'ordonancement de match
        context.getBean(PamiOrdonanceur.class).run();
        context.close();
        System.exit(0);
    }
}
