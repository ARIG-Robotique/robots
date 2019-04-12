package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import org.arig.robot.system.capteurs.RPLidarA2TelemeterOverSocket;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
@AllArgsConstructor
public class QuitCommand implements Quit.Command {

    private final RPLidarA2TelemeterOverSocket lidar;

    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit"})
    public void quit() {

        // Stop le lidar en quittant
        lidar.stopScan();
        lidar.end();

        throw new ExitRequest();
    }
}
