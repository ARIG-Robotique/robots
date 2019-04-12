package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.arig.robot.services.IIOService;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
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

    private final Abstract2WheelsEncoders encoders;
    private final AbstractPropulsionsMotors motors;
    private final IIOService ioService;

    private final List<InfoCapture> infos = new ArrayList<>();

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @SneakyThrows
    @ShellMethod("Capture des valeurs de codeurs des roues de propulsions")
    public void captureCodeursRoues() {
        // Vitesse positive
        log.info("Reset codeurs");
        encoders.reset();
        for (int vitesse = motors.getStopSpeed() ; vitesse <= motors.getMaxSpeed() ; vitesse++) {
            captureForVitesse(vitesse);
        }

        motors.stopAll();
        Thread.sleep(5000);

        // Vitesse négative
        log.info("Reset codeurs");
        encoders.reset();
        for (int vitesse = motors.getStopSpeed() - 1 ; vitesse >= motors.getMinSpeed() ; vitesse--) {
            captureForVitesse(vitesse);
        }

        motors.stopAll();

        // Ecriture en CSV
        List<String> lines = infos.parallelStream()
                .map(i -> String.format("%s;%s;%s", i.getVitesse(), i.getGauche(), i.getDroit()))
                .collect(Collectors.toList());
        IOUtils.writeLines(lines, "\n", new FileOutputStream("capture.csv"), Charset.defaultCharset());
    }

    @SneakyThrows
    private void captureForVitesse(int vitesse) {
        log.info("Vitesse moteur {}", vitesse);
        motors.generateMouvement(vitesse, vitesse);
        for(int mesure = 0 ; mesure < 10 ; mesure++) {
            Thread.sleep(10);

            encoders.lectureValeurs();
            infos.add(new InfoCapture(vitesse, encoders.getGauche(), encoders.getDroit()));
        }
    }

    @Data
    class InfoCapture {
        private final int vitesse;
        private final double gauche, droit;
    }
}
