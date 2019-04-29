package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ESide;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.PincesService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.validation.constraints.NotNull;

@Slf4j
@ShellComponent
@ShellCommandGroup("Pince")
@AllArgsConstructor
public class PinceCommands {

    private final PincesService pincesService;
    private final IIOService ioService;

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Prise sur la table")
    public void priseTable(@NotNull ESide side) {
        boolean prise = pincesService.priseTable(CouleurPalet.INCONNU, side);
        if (prise) {
            log.info("Prise sur table {}", side.name());
            pincesService.stockageAsync(side);
        } else {
            log.info("Pas d'element sur la table coté {}", side.name());
        }
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Dépose sur la balance")
    public void deposeBalance(@NotNull ESide side) {
        log.warn("Pas encore implémenté");
    }
}
