package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.NerellOrdonanceur;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.strategy.Action;
import org.springframework.shell.Availability;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
//@ShellComponent
//@ShellCommandGroup("Actions")
@AllArgsConstructor
public class NerellActionsCommands {

    private final AbstractEnergyService energyService;

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

    static class NerellActionsProvider implements ValueProvider {
        @Override
        public List<CompletionProposal> complete(CompletionContext completionContext) {
            NerellActionsCommands commands = (NerellActionsCommands) completionContext.getCommandRegistration().getTarget().getBean();
            return commands.actions.stream()
                    .filter(a -> a.getClass().getSimpleName().contains(completionContext.currentWordUpToCursor()))
                    .map(a -> new CompletionProposal(a.getClass().getSimpleName()))
                    .collect(Collectors.toList());
        }
    }
}
