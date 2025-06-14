package org.arig.robot.config.spring;

import lombok.SneakyThrows;
import org.arig.robot.OdinOrdonanceur;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.constants.OdinConstantesConfig;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.ramp.GainFactorRampFilter;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.system.encoders.BouchonARIG2WheelsEncoders;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class OdinSimulator {

  @SneakyThrows
  public static void main(final String[] args) {
    // Définition d'un ID unique pour le nommage des fichiers
    final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat));
    System.setProperty(ConstantesConfig.keyExecutionId, execId);

    ConfigurableApplicationContext context = SpringApplication.run(OdinSimulator.class, args);
    OdinRobotStatus rs = context.getBean(OdinRobotStatus.class);
    rs.simulateur(true);

    GainFactorRampFilter rampDistance = context.getBean("rampDistance", GainFactorRampFilter.class);
    rampDistance.setGain(OdinConstantesConfig.gainVitesseRampeDistanceSimulateur);

    GainFactorRampFilter rampOrientation = context.getBean("rampOrientation", GainFactorRampFilter.class);
    rampOrientation.setGain(OdinConstantesConfig.gainVitesseRampeOrientationSimulateur);

    PidFilter pidDistance = context.getBean("pidDistance", PidFilter.class);
    pidDistance.setTunings(OdinConstantesConfig.kpDistanceSimu, OdinConstantesConfig.kiDistanceSimu, OdinConstantesConfig.kdDistanceSimu);

    PidFilter pidOrientation = context.getBean("pidOrientation", PidFilter.class);
    pidOrientation.setTunings(OdinConstantesConfig.kpOrientationSimu, OdinConstantesConfig.kiOrientationSimu, OdinConstantesConfig.kdOrientationSimu);

    // Affichage des bornes pour le limiteur des moteurs
    context.getBean(BouchonARIG2WheelsEncoders.class).printLimiterValues();

    // Démarrage de l'ordonancement de match
    context.getBean(OdinOrdonanceur.class).run();
    context.close();
    System.exit(0);
  }
}
