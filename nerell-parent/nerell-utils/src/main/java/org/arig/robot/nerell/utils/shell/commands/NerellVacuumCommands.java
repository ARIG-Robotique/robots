package org.arig.robot.nerell.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.system.vacuum.ARIGVacuumController;
import org.arig.robot.system.vacuum.VacuumPumpState;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
@ShellCommandGroup("Vacuum")
@RequiredArgsConstructor
public class NerellVacuumCommands {

    private final NerellIOService ioService;
    private final AbstractEnergyService energyService;
    private final ARIGVacuumController vacuumController;

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Place une pompe dans un mode")
    public void pumpState(final int nb, final VacuumPumpState state) {
        switch (state) {
            case OFF:
                vacuumController.off(nb);
                break;
            case ON:
                vacuumController.on(nb);
                break;
            case ON_FORCE:
                vacuumController.onForce(nb);
                break;
            case DISABLED:
                vacuumController.disable(nb);
                break;
        }
    }

    @ShellMethod("Pompe version")
    public void pumpVersion() {
        vacuumController.printVersion();
    }
}
