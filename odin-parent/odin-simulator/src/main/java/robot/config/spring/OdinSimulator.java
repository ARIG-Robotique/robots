package robot.config.spring;

import lombok.SneakyThrows;
import org.arig.robot.OdinOrdonanceur;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IGainFactorRampFilter;
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
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(IConstantesConfig.executiondIdFormat));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);

        ConfigurableApplicationContext context = SpringApplication.run(OdinSimulator.class, args);
        OdinRobotStatus rs = context.getBean(OdinRobotStatus.class);
        rs.simulateur(true);

        IGainFactorRampFilter rampDistance = context.getBean("rampDistance", IGainFactorRampFilter.class);
        rampDistance.setGain(IConstantesOdinConfig.gainVitesseRampeDistanceSimulateur);

        IGainFactorRampFilter rampOrientation = context.getBean("rampOrientation", IGainFactorRampFilter.class);
        rampOrientation.setGain(IConstantesOdinConfig.gainVitesseRampeOrientationSimulateur);

        IPidFilter pidDistance = context.getBean("pidDistance", IPidFilter.class);
        pidDistance.setTunings(IConstantesOdinConfig.kpDistanceSimu, IConstantesOdinConfig.kiDistanceSimu, IConstantesOdinConfig.kdDistanceSimu);

        IPidFilter pidOrientation = context.getBean("pidOrientation", IPidFilter.class);
        pidOrientation.setTunings(IConstantesOdinConfig.kpOrientationSimu, IConstantesOdinConfig.kiOrientationSimu, IConstantesOdinConfig.kdOrientationSimu);

        // Affichage des bornes pour le limiteur des moteurs
        context.getBean(BouchonARIG2WheelsEncoders.class).printLimiterValues();

        // Démarrage de l'ordonancement de match
        context.getBean(OdinOrdonanceur.class).run();
        context.close();
        System.exit(0);
    }
}
