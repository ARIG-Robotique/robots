package org.arig.robot.nerell.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.NerellServosService;
import org.arig.robot.system.vacuum.ARIGVacuumController;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
@ShellCommandGroup("Moteurs")
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
    @ShellMethod("Pompe on")
    public void pumpOn(final int nb) {
        vacuumController.on(nb);
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Pompe off")
    public void pumpOff(final int nb) {
        vacuumController.off(nb);
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Pompe disable")
    public void pumpDisable(final int nb) {
        vacuumController.disable(nb);
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Pompe test")
    public void pumpTest(final int nb) {
        nerellServosService.ascenseurAvantBas(nb - 1, true);
        vacuumController.on(nb);
        ThreadUtils.sleep(5000);
        nerellServosService.ascenseurAvantHaut(nb - 1, true);
        ThreadUtils.sleep(5000);
        nerellServosService.ascenseurAvantBas(nb - 1, true);
        vacuumController.off(nb);
    }
}
