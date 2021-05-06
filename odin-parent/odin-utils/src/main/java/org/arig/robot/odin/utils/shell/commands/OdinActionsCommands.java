package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.OdinOrdonanceur;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.odin.utils.shell.providers.OdinActionsProvider;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.strategy.IAction;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;

@Slf4j
@ShellComponent
@ShellCommandGroup("Actions")
@AllArgsConstructor
public class OdinActionsCommands {

    private IOdinIOService ioService;
    private OdinOrdonanceur ordonanceur;
    private List<IAction> actions;
    private final OdinRobotStatus rs;

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Calage bordure")
    @SneakyThrows
    public void calageBordure() {
        ordonanceur.calageBordure(false);
    }

    @ShellMethod("Activation des services")
    public void enableServices() {
    }

    @ShellMethod("Désactivation des services")
    public void disableServices() {
    }

    @ShellMethod("Execute une action")
    @SneakyThrows
    public void action(@ShellOption(valueProvider = OdinActionsProvider.class) String name) {
        final Optional<IAction> action = actions.stream()
                .filter(a -> a.getClass().getSimpleName().equals(name))
                .findFirst();

        if (action.isPresent()) {
            log.info("Execution de l'action {}, ordre {}, valide {}", action.get().name(), action.get().order(), action.get().isValid());
            action.get().execute();
        } else {
            log.warn("Pas d'action nommée \"{}\" trouvée", name);
        }
    }

}
