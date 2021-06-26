package org.arig.robot.nerell.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.NerellServosService;
import org.arig.robot.system.vacuum.ARIGVacuumController;
import org.arig.robot.system.vacuum.VacuumPumpState;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
@ShellCommandGroup("Vacuum")
@RequiredArgsConstructor
public class NerellVacuumCommands {

    private final AbstractRobotStatus rs;
    private final INerellIOService ioService;
    private final ARIGVacuumController vacuumController;
    private final NerellServosService nerellServosService;

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
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
        nerellServosService.ascenseurAvantBas(nb - 1, true);
        vacuumController.on(nb);
        ThreadUtils.sleep(5000);
        nerellServosService.ascenseurAvantHaut(nb - 1, true);
        ThreadUtils.sleep(5000);
        vacuumController.onForce(nb);
        nerellServosService.ascenseurAvantBas(nb - 1, true);
        vacuumController.off(nb);
    }
}
