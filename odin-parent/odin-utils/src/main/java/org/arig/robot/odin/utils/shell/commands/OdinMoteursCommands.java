package org.arig.robot.odin.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
@ShellCommandGroup("Moteurs")
@RequiredArgsConstructor
public class OdinMoteursCommands {

    private final AbstractRobotStatus rs;
    private final IOdinIOService ioService;
    private final AbstractEnergyService energyService;
    private final AbstractPropulsionsMotors propulsionsMotors;

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Rotation des moteurs de propulsions")
    public void moteursPropulsions(final int droite, final int gauche) {
        rs.enableCapture();
        propulsionsMotors.generateMouvement(gauche, droite);
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Arret des moteurs de propulsions")
    public void stopMoteursPropulsions() {
        propulsionsMotors.generateMouvement(propulsionsMotors.getStopSpeed(), propulsionsMotors.getStopSpeed());
        rs.disableCapture();
    }
}
