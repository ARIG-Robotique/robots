package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.services.OdinServosService;
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
public class OdinServosCommands {

    private final OdinRobotStatus rs;
    private final OdinServosService servosService;
    private final IOdinIOService ioService;

    private final int nbLoop = 3;

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Récupèration de tension des servos")
    public void getTension() {
        final double tension = servosService.getTension();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
        ThreadUtils.sleep(800);
    }

    @ShellMethod("Configuration attente bras gauche")
    public void configWaitBrasGauche(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.brasGauchePhare(false);
            ThreadUtils.sleep(wait);
            servosService.brasGaucheFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente bras droit")
    public void configWaitBrasDroit(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.brasDroitPhare(false);
            ThreadUtils.sleep(wait);
            servosService.brasDroitFerme(false);
            ThreadUtils.sleep(wait);
        }
    }
}
