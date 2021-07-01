package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.NerellServosService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@AllArgsConstructor
public class NerellAlimentationCommands {

    private final INerellIOService ioService;
    private final NerellServosService servosService;

    public Availability auOK() {
        return ioService.auOk() ? Availability.available() : Availability.unavailable("Arret d'urgence non OK");
    }

    @ShellMethodAvailability("auOK")
    @ShellMethod("Activation des alimentations")
    public void enableAlimentation() {
        servosService.cyclePreparation();
        ioService.enableAlimServos();
        ioService.enableAlimMoteurs();
    }

    @ShellMethod("Désactivation des alimentations")
    public void disableAlimentation() {
        ioService.disableAlimServos();
        ioService.disableAlimMoteurs();
    }
}
