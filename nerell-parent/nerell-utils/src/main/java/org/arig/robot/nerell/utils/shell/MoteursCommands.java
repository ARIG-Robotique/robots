package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import org.arig.robot.services.IIOService;
import org.arig.robot.system.motors.AbstractMotor;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
@ShellCommandGroup("Moteurs")
@AllArgsConstructor
public class MoteursCommands {

    private final IIOService ioService;
    private final AbstractPropulsionsMotors propulsionsMotors;
    private final AbstractMotor carouselMotor;

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Rotation des moteurs de propulsions")
    public void moteursPropulsions(final int droite, final int gauche) {
        propulsionsMotors.generateMouvement(gauche, droite);
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Rotation du Carousel")
    public void moteurCarousel(int value) {
        carouselMotor.speed(value);
    }
}
