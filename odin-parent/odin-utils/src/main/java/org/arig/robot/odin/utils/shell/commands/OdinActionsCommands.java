package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.OdinOrdonanceur;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.OdinIOService;
import org.arig.robot.strategy.Action;
import org.springframework.shell.Availability;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ValueProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ShellComponent
@ShellCommandGroup("Actions")
@AllArgsConstructor
public class OdinActionsCommands {

  private final AbstractEnergyService energyService;

  private OdinIOService ioService;
  private OdinOrdonanceur ordonanceur;
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
  public void action(@ShellOption(valueProvider = OdinActionsProvider.class) String name) {
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

  static class OdinActionsProvider implements ValueProvider {
    @Override
    public List<CompletionProposal> complete(CompletionContext completionContext) {
      OdinActionsCommands commands = (OdinActionsCommands) completionContext.getCommandRegistration().getTarget().getBean();
      return commands.actions.stream()
        .filter(a -> a.getClass().getSimpleName().contains(completionContext.currentWordUpToCursor()))
        .map(a -> new CompletionProposal(a.getClass().getSimpleName()))
        .collect(Collectors.toList());
    }
  }
}
