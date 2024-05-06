package org.arig.robot.pami.utils.shell.commands;

import lombok.AllArgsConstructor;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
@AllArgsConstructor
public class PamiQuitCommand implements Quit.Command {

    private final AbstractPropulsionsMotors motors;
    private final PamiIOService ioService;

    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit"})
    public void quit() {
        // Stop les moteurs
        motors.stopAll();

        // Stop les alimentations de puissance
        ioService.disableAlimMoteurs();
        ioService.disableAlimServos();

        throw new ExitRequest();
    }
}
