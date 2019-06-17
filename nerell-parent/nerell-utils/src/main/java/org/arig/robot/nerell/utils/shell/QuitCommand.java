package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import org.arig.robot.services.IIOService;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
@AllArgsConstructor
public class QuitCommand implements Quit.Command {

    private final ILidarTelemeter lidar;
    private final IIOService ioService;

    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit"})
    public void quit() {

        // Stop le lidar en quittant
        lidar.stopScan();
        lidar.end();

        // Stop les alimentations de puissance
        ioService.disableAlim12VPuissance();
        ioService.disableAlim5VPuissance();

        // Stop les pompes a vide
        ioService.videElectroVanneDroite();
        ioService.videElectroVanneGauche();
        ioService.disablePompeAVideDroite();
        ioService.disablePompeAVideGauche();

        throw new ExitRequest();
    }
}
