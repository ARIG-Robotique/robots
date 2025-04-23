package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellRobotServosService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class NerellServosCommands {

    private final NerellRobotServosService servosService;
    private final NerellIOService ioService;
    private final AbstractEnergyService energyService;

    private final int nbLoop = 5;

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos()
                ? Availability.available() : Availability.unavailable("Alimentation servos KO");
    }

    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
        ThreadUtils.sleep(800);
    }

    @ShellMethod("Identification des servos")
    public void identificationServos(byte id, int delta, byte speed, int nbCycle) {
        for (int i = 0; i < nbCycle; i++) {
            servosService.setPositionById(id, 1500 + delta, speed);
            ThreadUtils.sleep(1000);
            servosService.setPositionById(id, 1500 - delta, speed);
            ThreadUtils.sleep(1000);
        }
        servosService.setPositionById(id, 1500, speed);
    }

    @ShellMethod("Configuration attente pince")
    public void configWaitPinceAvant(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupePincesAvantPrise(false);
            ThreadUtils.sleep(wait);
            servosService.groupePincesAvantFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente ski")
    public void configWaitTiroirAvant(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.tirroirAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.tirroirAvantStock(false);
            ThreadUtils.sleep(wait);
        }
    }
}
