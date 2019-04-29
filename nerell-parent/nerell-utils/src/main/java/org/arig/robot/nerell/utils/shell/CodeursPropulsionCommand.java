package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.arig.robot.services.IIOService;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.encoders.AbstractEncoder;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
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
@AllArgsConstructor
@ShellCommandGroup("Codeurs Propulsion")
public class CodeursPropulsionCommand {

    private final AbstractEncoder carouselEncoder;
    private final Abstract2WheelsEncoders wheelsEncoders;
    private final AbstractPropulsionsMotors propulsionsMotors;
    private final IIOService ioService;

    private final List<InfoCapture> infos = new ArrayList<>();

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @SneakyThrows
    @ShellMethod("Lecture des codeurs des roues de propulsions")
    public void readCodeursRoues() {
        wheelsEncoders.lectureValeurs();
        log.info("Gauche : {} - Droite : {}", wheelsEncoders.getGauche(), wheelsEncoders.getDroit());
    }

    @SneakyThrows
    @ShellMethod("Lecture du codeur carousel")
    public void readCodeurCarousel() {
        carouselEncoder.lectureValeur();
        log.info("Carousel : {}", carouselEncoder.getValue());
    }

    @SneakyThrows
    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Capture des valeurs de codeurs des roues de propulsions")
    public void captureCodeursRoues() {
        // Vitesse positive
        log.info("Reset codeurs");
        wheelsEncoders.reset();
        for (int vitesse = propulsionsMotors.getStopSpeed(); vitesse <= propulsionsMotors.getMaxSpeed() ; vitesse++) {
            captureForVitesse(vitesse);
        }

        propulsionsMotors.stopAll();
        Thread.sleep(5000);

        // Vitesse négative
        log.info("Reset codeurs");
        wheelsEncoders.reset();
        for (int vitesse = propulsionsMotors.getStopSpeed() - 1; vitesse >= propulsionsMotors.getMinSpeed() ; vitesse--) {
            captureForVitesse(vitesse);
        }

        propulsionsMotors.stopAll();

        // Ecriture en CSV
        List<String> lines = infos.parallelStream()
                .map(i -> String.format("%s;%s;%s", i.getVitesse(), i.getGauche(), i.getDroit()))
                .collect(Collectors.toList());
        IOUtils.writeLines(lines, "\n", new FileOutputStream("capture.csv"), Charset.defaultCharset());
    }

    @SneakyThrows
    private void captureForVitesse(int vitesse) {
        log.info("Vitesse moteur {}", vitesse);
        propulsionsMotors.generateMouvement(vitesse, vitesse);
        for(int mesure = 0 ; mesure < 10 ; mesure++) {
            Thread.sleep(10);

            wheelsEncoders.lectureValeurs();
            infos.add(new InfoCapture(vitesse, wheelsEncoders.getGauche(), wheelsEncoders.getDroit()));
        }
    }

    @Data
    class InfoCapture {
        private final int vitesse;
        private final double gauche, droit;
    }
}
