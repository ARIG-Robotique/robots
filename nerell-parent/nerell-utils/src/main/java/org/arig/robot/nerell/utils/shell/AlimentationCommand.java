package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.IIOService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@AllArgsConstructor
public class AlimentationCommand {

    private final IIOService ioService;

    @ShellMethodAvailability({"enableAlimentation"})
    public Availability auOK() {
        return ioService.auOk() ? Availability.available() : Availability.unavailable("Arret d'urgence non OK");
    }

    @ShellMethod
    public void enableAlimentation() {
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();
    }

    @ShellMethod
    public void disableAlimentation() {
        ioService.disableAlim5VPuissance();
        ioService.disableAlim12VPuissance();
    }
}
