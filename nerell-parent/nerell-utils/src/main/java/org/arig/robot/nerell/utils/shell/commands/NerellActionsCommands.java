package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.NerellOrdonanceur;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.nerell.utils.shell.providers.NerellActionsProvider;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.strategy.Action;
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
public class NerellActionsCommands {

    private final AbstractEnergyService energyService;
    private final NerellRobotStatus rs;

    private NerellIOService ioService;
    private NerellOrdonanceur ordonanceur;
    private List<Action> actions;

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos() && energyService.checkMoteurs()
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
    public void action(@ShellOption(valueProvider = NerellActionsProvider.class) String name) {
        final Optional<Action> action = actions.stream()
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
