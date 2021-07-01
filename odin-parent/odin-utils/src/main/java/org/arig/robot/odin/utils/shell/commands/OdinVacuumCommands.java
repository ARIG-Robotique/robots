package org.arig.robot.odin.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.system.vacuum.ARIGVacuumController;
import org.arig.robot.system.vacuum.VacuumPumpState;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@ShellCommandGroup("Vacuum")
@RequiredArgsConstructor
public class OdinVacuumCommands {

    private final IOdinIOService ioService;
    private final AbstractEnergyService energyService;
    private final ARIGVacuumController vacuumController;

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Place une pompe dans un mode")
    public void pumpState(final int nb, final VacuumPumpState state) {
        switch(state) {
            case OFF:      vacuumController.off(nb);break;
            case ON:       vacuumController.on(nb);break;
            case ON_FORCE: vacuumController.onForce(nb);break;
            case DISABLED: vacuumController.disable(nb);break;
        }
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Pompe test")
    public void pumpTest(final int nb) {
        vacuumController.on(nb);
        ThreadUtils.sleep(5000);
        vacuumController.off(nb);
    }

    @ShellMethod("Pompe version")
    public void pumpVersion() {
        vacuumController.printVersion();
    }
}
