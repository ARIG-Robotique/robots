package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.CarouselService;
import org.arig.robot.services.IIOService;
import org.arig.robot.system.ICarouselManager;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@ShellCommandGroup("Carousel")
@AllArgsConstructor
public class CarouselCommands {

    private final IIOService ioService;
    private final CarouselService carouselService;
    private final ICarouselManager carouselManager;

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Lecture de la couleur d'un atome")
    public void lectureCouleur(int index) {
        carouselManager.setColor(index, CouleurPalet.INCONNU);
        carouselService.lectureCouleurAsync(index);
    }

}
