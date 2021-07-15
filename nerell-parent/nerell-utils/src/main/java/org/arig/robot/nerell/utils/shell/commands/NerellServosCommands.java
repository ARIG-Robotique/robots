package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.NerellServosService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class NerellServosCommands {

    private final NerellRobotStatus rs;
    private final NerellServosService servosService;
    private final INerellIOService ioService;
    private final AbstractEnergyService energyService;

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
        ThreadUtils.sleep(800);
    }

}
