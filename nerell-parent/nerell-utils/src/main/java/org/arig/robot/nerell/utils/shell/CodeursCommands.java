package org.arig.robot.nerell.utils.shell;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.arig.robot.services.IIOService;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.encoders.AbstractEncoder;
import org.arig.robot.system.motors.AbstractMotor;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class CodeursCommands {

    private final AbstractEncoder carouselEncoder;
    private final Abstract2WheelsEncoders wheelsEncoders;
    private final AbstractPropulsionsMotors propulsionsMotors;
    private final IIOService ioService;

    @Autowired
    @Qualifier("motorCarousel")
    private AbstractMotor carouselMotor;

    private final List<InfoCapturePropulsions> infosPropulsions = new ArrayList<>();
    private final List<InfoCaptureCarousel> infosCarousel = new ArrayList<>();

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
    public void captureCodeursPropulsions() {
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

        // Ecriture en CSV
        List<String> lines = infosPropulsions.parallelStream()
                .map(i -> String.format("%s;%s;%s", i.getVitesse(), i.getGauche(), i.getDroit()))
                .collect(Collectors.toList());
        IOUtils.writeLines(lines, "\n", new FileOutputStream("capture-propulsions.csv"), Charset.defaultCharset());
    }

    @SneakyThrows
    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Capture des valeurs du codeur du carousel")
    public void captureCodeurCarousel() {
        // Vitesse positive
        log.info("Reset codeurs");
        carouselEncoder.reset();
        for (int vitesse = carouselMotor.getStopSpeed(); vitesse <= carouselMotor.getMaxSpeed(); vitesse++) {
            captureCarouselForVitesse(vitesse);
        }

        carouselMotor.stop();
        Thread.sleep(5000);

        // Vitesse négative
        log.info("Reset codeurs");
        carouselEncoder.reset();
        for (int vitesse = carouselMotor.getStopSpeed() - 1; vitesse >= carouselMotor.getMinSpeed(); vitesse--) {
            captureCarouselForVitesse(vitesse);
        }

        carouselMotor.stop();

        // Ecriture en CSV
        List<String> lines = infosCarousel.parallelStream()
                .map(i -> String.format("%s;%s", i.getVitesse(), i.getValue()))
                .collect(Collectors.toList());
        IOUtils.writeLines(lines, "\n", new FileOutputStream("capture-carousel.csv"), Charset.defaultCharset());
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Calibration carousel")
    public void calibrationCarousel() {

        carouselEncoder.reset();

        List<Double> values = new ArrayList<>();
        double sum = 0;
        int nbtours = 50;

        for (int mesure = 0; mesure <= nbtours; mesure++) {
            carouselMotor.speed(100);

            ThreadUtils.sleep(2000);

            while (!ioService.indexCarousel()) {
                ThreadUtils.sleep(10);
            }

            carouselMotor.speed(-80);

            while (ioService.indexCarousel()) {
                ThreadUtils.sleep(10);
            }

            carouselMotor.speed(80);

            while (!ioService.indexCarousel()) {
                ThreadUtils.sleep(10);
            }

            carouselMotor.stop();
            carouselEncoder.lectureValeur();

            if (mesure > 0) {
                double value = carouselEncoder.getValue();
                log.info("Valeur : {}", value);
                values.add(value);
                sum += value;
            }
        }

        log.info("Valeurs rotation : {}", values);
        log.info("Moyenne : {}", sum / nbtours);
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

    @SneakyThrows
    private void captureCarouselForVitesse(int vitesse) {
        log.info("Vitesse moteur carousel {}", vitesse);
        carouselMotor.speed(vitesse);
        for (int mesure = 0; mesure < 10; mesure++) {
            Thread.sleep(10);

            carouselEncoder.lectureValeur();
            infosCarousel.add(new InfoCaptureCarousel(vitesse, carouselEncoder.getValue()));
        }
    }

    @Data
    class InfoCapturePropulsions {
        private final int vitesse;
        private final double gauche, droit;
    }

    @Data
    class InfoCaptureCarousel {
        private final int vitesse;
        private final double value;
    }
}
