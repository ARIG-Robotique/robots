package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class ServosCommands {

    private final ServosService servosService;
    private final IIOService ioService;

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
    }

    @ShellMethod("Récupèration de tension des servos")
    public void getTension() {
        final double tension = servosService.getTension();
        log.info("La tension actuelle des servos {}", tension);
    }
}
