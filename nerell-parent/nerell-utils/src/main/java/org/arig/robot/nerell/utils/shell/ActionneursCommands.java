package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ESide;
import org.arig.robot.model.EState;
import org.arig.robot.services.IIOService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.validation.constraints.NotNull;

@Slf4j
@ShellComponent
@ShellCommandGroup("Actionneurs")
@AllArgsConstructor
public class ActionneursCommands {

    private final IIOService ioService;

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Electrovanne")
    public void electrovanne(@NotNull final ESide side, @NotNull final EState state) {
        log.info("Electrovanne {} : {}", side.name(), state.name());

        if (side == ESide.DROITE) {
            if (state == EState.ON) {
                ioService.airElectroVanneDroite();
            } else {
                ioService.videElectroVanneDroite();
            }
        } else {
            if (state == EState.ON) {
                ioService.airElectroVanneGauche();
            } else {
                ioService.videElectroVanneGauche();
            }
        }
    }

    @ShellMethod(value = "Pompe a vide")
    public void pompe(@NotNull final ESide side, @NotNull final EState state) {
        log.info("Pompe a vide {} : {}", side.name(), state.name());

        if (side == ESide.DROITE) {
            if (state == EState.ON) {
                ioService.enablePompeAVideDroite();
            } else {
                ioService.disablePompeAVideDroite();
            }
        } else {
            if (state == EState.ON) {
                ioService.enablePompeAVideGauche();
            } else {
                ioService.disablePompeAVideGauche();
            }
        }
    }
}
