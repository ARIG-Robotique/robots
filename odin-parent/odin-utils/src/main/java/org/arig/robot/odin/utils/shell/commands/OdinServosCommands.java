package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.OdinIOService;
import org.arig.robot.services.OdinRobotServosService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class OdinServosCommands {

    private final OdinRobotStatus rs;
    private final OdinRobotServosService servosService;
    private final BrasService brasService;
    private final OdinIOService ioService;
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

    enum Bras {
        AVANT_GAUCHE,
        AVANT_CENTRE
    }

    @ShellMethod("Déplacement de bras")
    public void mouvementBras(Bras bras, int a1, int a2, int a3) {
        switch (bras) {
            case AVANT_GAUCHE:
                servosService.brasAvantGauche(a1, a2, a3, 40);
                break;
            case AVANT_CENTRE:
                servosService.brasAvantCentre(a1, a2, a3, 40);
                break;
        }
    }
}
