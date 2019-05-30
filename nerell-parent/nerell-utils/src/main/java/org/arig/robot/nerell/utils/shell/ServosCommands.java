package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.validation.constraints.NotNull;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class ServosCommands {

    private final ServosService servosService;
    private final IIOService ioService;

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Gestion des portes")
    public void portes(@NotNull Boolean open) {
        if (open) {
            servosService.porteBarilletDroit(IConstantesServos.PORTE_BARILLET_DROIT_OUVERT, false);
            servosService.porteBarilletGauche(IConstantesServos.PORTE_BARILLET_GAUCHE_OUVERT, true);
        } else {
            servosService.porteBarilletDroit(IConstantesServos.PORTE_BARILLET_DROIT_FERME, false);
            servosService.porteBarilletGauche(IConstantesServos.PORTE_BARILLET_GAUCHE_FERME, true);
        }
    }

    @ShellMethod("Récupèration de tension des servos")
    public void getTension() {
        final double tension = servosService.getTension();
    }
}
