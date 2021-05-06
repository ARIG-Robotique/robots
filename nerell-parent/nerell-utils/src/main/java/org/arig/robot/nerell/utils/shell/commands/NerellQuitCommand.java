package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.motors.AbstractMotor;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
@AllArgsConstructor
public class NerellQuitCommand implements Quit.Command {

    private final ILidarTelemeter lidar;
    private final INerellIOService ioService;
    private final AbstractMotor motorPavillon;

    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit"})
    public void quit() {

        // Stop le lidar en quittant
        lidar.stopScan();
        lidar.end();

        // Stop le moteur du pavillon
        motorPavillon.speed(motorPavillon.getStopSpeed());

        // Stop les alimentations de puissance
        ioService.disableAlim12VPuissance();
        ioService.disableAlim5VPuissance();

        throw new ExitRequest();
    }
}
