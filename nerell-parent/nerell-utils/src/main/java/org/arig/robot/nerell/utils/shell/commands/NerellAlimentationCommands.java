package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellServosService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@AllArgsConstructor
public class NerellAlimentationCommands {

    private final NerellIOService ioService;
    private final NerellServosService servosService;
    //private final IAlimentationSensor alimentationSensor;

    public Availability auOK() {
        return ioService.auOk() ? Availability.available() : Availability.unavailable("Arret d'urgence non OK");
    }

    @ShellMethodAvailability("auOK")
    @ShellMethod("Activation aliemntation moteurs")
    public void enableAlimentationMoteurs() {
        ioService.enableAlimMoteurs();
    }

    @ShellMethodAvailability("auOK")
    @ShellMethod("Activation aliemntation servos")
    public void enableAlimentationServos() {
        servosService.cyclePreparation();
        ioService.enableAlimServos();
    }

    @ShellMethod("Désactivation des alimentations")
    public void disableAlimentation() {
        ioService.disableAlimServos();
        ioService.disableAlimMoteurs();
    }

    /*@SneakyThrows
    @ShellMethod("Lecture des alimentations")
    public void readAlimentation(int nbRead) {
        alimentationSensor.printVersion();
        for (int read = 0 ; read < nbRead ; read++) {
            log.info("Lecture {} / {}", read + 1, nbRead);
            alimentationSensor.refresh();
            for (byte i = 1; i <= 2; i++) {
                AlimentationSensorValue v = alimentationSensor.get(i);
                log.info("Lecture channel {} ({})\t{} V\t{} A",
                        i, v.fault() ? "en erreur" : "OK", v.tension(), v.current());
            }

            ThreadUtils.sleep(200);
        }
    }*/
}
