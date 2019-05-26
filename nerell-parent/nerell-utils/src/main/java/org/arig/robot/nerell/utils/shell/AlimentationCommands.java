package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@AllArgsConstructor
public class AlimentationCommands {

    private final IIOService ioService;
    private final ServosService servosService;
    private final RobotStatus rs;

    public Availability auOK() {
        return ioService.auOk() ? Availability.available() : Availability.unavailable("Arret d'urgence non OK");
    }

    @ShellMethodAvailability("auOK")
    @ShellMethod("Activation des alimentations")
    public void enableAlimentation() {
        servosService.cyclePreparation();
        ioService.enableAlim5VPuissance();
        ioService.enableAlim12VPuissance();
    }

    @ShellMethod("Désactivation des alimentations")
    public void disableAlimentation() {
        ioService.disableAlim5VPuissance();
        ioService.disableAlim12VPuissance();
    }

    @ShellMethod("Activation des services")
    public void enableStocksServices() {
        rs.enableAsservCarousel();
        rs.enableCarousel();
        rs.enableMagasin();
        rs.enableSerrage();
        rs.enableVentouses();
    }
}
