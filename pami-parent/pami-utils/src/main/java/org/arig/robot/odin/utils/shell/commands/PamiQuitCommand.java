package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.vacuum.ARIGVacuumController;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
@AllArgsConstructor
public class PamiQuitCommand implements Quit.Command {

    private final ILidarTelemeter lidar;
    private final PamiIOService ioService;
    private final ARIGVacuumController vacuumController;

    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit"})
    public void quit() {
        // Stop le lidar en quittant
        lidar.stopScan();
        lidar.end();

        // Désactivation des pompes
        vacuumController.disableAll();

        // Stop les alimentations de puissance
        ioService.disableAlimMoteurs();
        ioService.disableAlimServos();

        throw new ExitRequest();
    }
}
