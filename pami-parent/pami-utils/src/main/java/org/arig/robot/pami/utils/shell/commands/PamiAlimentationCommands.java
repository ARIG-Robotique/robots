package org.arig.robot.pami.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.capteurs.AlimentationSensorValue;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.services.PamiRobotServosService;
import org.arig.robot.system.capteurs.can.ARIG2024AlimentationController;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@AllArgsConstructor
public class PamiAlimentationCommands {

    private final PamiIOService ioService;
    private final PamiRobotServosService servosService;
    private final ARIG2024AlimentationController alimentationController;

    public Availability auOK() {
        return ioService.auOk() ? Availability.available() : Availability.unavailable("Arret d'urgence non OK");
    }

    @ShellMethod("Faire un son avec la carte alim")
    public void soundAlimentation() {
        alimentationController.sound();
    }

    @SneakyThrows
    @ShellMethod("Faire un scan des infos de l'alim")
    public void scanAlimentation() {
        alimentationController.scan();
        alimentationController.printVersion();
    }

    @ShellMethodAvailability("auOK")
    @ShellMethod("Activation alimentation moteurs")
    public void enableAlimentationMoteurs() {
        ioService.enableAlimMoteurs();
    }

    @ShellMethodAvailability("auOK")
    @ShellMethod("Activation alimentation servos")
    public void enableAlimentationServos() {
        servosService.cyclePreparation();
        ioService.enableAlimServos();
    }

    @ShellMethodAvailability("auOK")
    @ShellMethod("Activation alimentations")
    public void enableAlimentation() {
        enableAlimentationMoteurs();
        enableAlimentationServos();
    }

    @ShellMethod("Désactivation des alimentations")
    public void disableAlimentation() {
        ioService.disableAlimServos();
        ioService.disableAlimMoteurs();
    }

    @SneakyThrows
    @ShellMethod("Lecture des alimentations")
    public void readAlimentation(int nbRead) {
        alimentationController.printVersion();
        for (int read = 0 ; read < nbRead ; read++) {
            log.info("Lecture {} / {}", read + 1, nbRead);
            alimentationController.refresh();
            for (byte i = 1; i <= 2; i++) {
                AlimentationSensorValue v = alimentationController.get(i);
                log.info("Lecture channel {} ({})\t{} V\t{} A",
                        i, v.fault() ? "en erreur" : "OK", v.tension(), v.current());
            }

            ThreadUtils.sleep(2000);
        }
    }
}
