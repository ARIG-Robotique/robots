package org.arig.robot.nerell.utils.shell.commands;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
@ShellCommandGroup("Codeurs")
public class NerellCodeursCommands {

    private final AbstractRobotStatus rs;
    private final Abstract2WheelsEncoders wheelsEncoders;
    private final AbstractPropulsionsMotors propulsionsMotors;
    private final INerellIOService ioService;
    private final AbstractEnergyService energyService;

    private final List<InfoCapturePropulsions> infosPropulsions = new ArrayList<>();

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @SneakyThrows
    @ShellMethod("Lecture des codeurs des roues de propulsions")
    public void readCodeursRoues() {
        wheelsEncoders.lectureValeurs();
        log.info("Gauche : {} - Droite : {}", wheelsEncoders.getGauche(), wheelsEncoders.getDroit());
    }

    @SneakyThrows
    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Capture des valeurs de codeurs des roues de propulsions")
    public void captureCodeursPropulsions() {
        rs.enableCapture();
        ThreadUtils.sleep(2000);

        // Vitesse positive
        log.info("Reset codeurs");
        wheelsEncoders.reset();
        for (int vitesse = propulsionsMotors.getStopSpeed(); vitesse <= propulsionsMotors.getMaxSpeed(); vitesse++) {
            capturePropulsionsForVitesse(vitesse);
        }

        propulsionsMotors.stopAll();
        Thread.sleep(5000);

        // Vitesse négative
        log.info("Reset codeurs");
        wheelsEncoders.reset();
        for (int vitesse = propulsionsMotors.getStopSpeed() - 1; vitesse >= propulsionsMotors.getMinSpeed(); vitesse--) {
            capturePropulsionsForVitesse(vitesse);
        }

        propulsionsMotors.stopAll();

        ThreadUtils.sleep(2000);
        rs.disableCapture();

        // Ecriture en CSV
        List<String> lines = infosPropulsions.parallelStream()
                .map(i -> String.format("%s;%s;%s", i.getVitesse(), i.getGauche(), i.getDroit()))
                .collect(Collectors.toList());
        IOUtils.writeLines(lines, "\n", new FileOutputStream("capture-propulsions.csv"), Charset.defaultCharset());
    }

    @SneakyThrows
    private void capturePropulsionsForVitesse(int vitesse) {
        log.info("Vitesse moteurs propulsions {}", vitesse);
        propulsionsMotors.generateMouvement(vitesse, vitesse);
        for (int mesure = 0; mesure < 10; mesure++) {
            Thread.sleep(10);

            wheelsEncoders.lectureValeurs();
            infosPropulsions.add(new InfoCapturePropulsions(vitesse, wheelsEncoders.getGauche(), wheelsEncoders.getDroit()));
        }
    }

    @Data
    class InfoCapturePropulsions {
        private final int vitesse;
        private final double gauche, droit;
    }

}
