package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import org.arig.robot.model.CommandeAsservissementPosition;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.services.IIOService;
import org.arig.robot.utils.ConvertionCarouselUnit;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.validation.constraints.NotNull;

@ShellComponent
@ShellCommandGroup("Asservissement")
@AllArgsConstructor
public class AsservissementCommands {

    private final IIOService ioService;
    private final RobotStatus rs;
    private final ConvertionRobotUnit convRobot;
    private final ConvertionCarouselUnit convCarousel;
    private final CommandeRobot cmdRobot;
    private final CommandeAsservissementPosition cmdAsservCarousel;

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Asservissement du robot")
    public void asservRobot(@NotNull TypeConsigne[] typeConsignes, long distance, long orientation) {
        cmdRobot.setTypes(typeConsignes);
        cmdRobot.getVitesse().setDistance(100);
        cmdRobot.getVitesse().setOrientation(100);
        cmdRobot.getConsigne().setDistance((long) convRobot.mmToPulse(distance));
        cmdRobot.getConsigne().setOrientation((long) convRobot.degToPulse(orientation));
        cmdRobot.setFrein(true);

        rs.enableAsserv();
    }

    @ShellMethod("Désactivation asservissement du robot")
    public void disableAsservRobot() {
        rs.disableAsserv();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Asservissement du Carousel")
    public void asservCarousel(int index) {
        cmdAsservCarousel.getVitesse().setValue(100);
        cmdAsservCarousel.getConsigne().setValue(convCarousel.indexToPulse(index));
        cmdAsservCarousel.setFrein(true);

        rs.enableAsservCarousel();
    }

    @ShellMethod("Désactivation asservissement du carousel")
    public void disableAsservCarousel() {
        rs.disableAsservCarousel();
    }
}
