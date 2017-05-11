package org.arig.robot.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Ordonancement des actions a réaliser.
 *
 * @author gdepuille on 06/05/15.
 */
@Slf4j
public class StrategyManager {

    @Autowired
    private List<IAction> actions;

    private List<IAction> completedActions = new ArrayList<>();

    public void execute() {
        log.info("Recherche d'une statégie à éxécuter parmis les {} actions disponible", actionsCount());

        // On recherche la stratégy adapté.
        Optional<IAction> nextAction = actions.stream()
                .filter(IAction::isValid)
                .sorted(Comparator.comparingInt(IAction::order).reversed())
                .findFirst();

        if (!nextAction.isPresent()) {
            log.warn("0/{} actions disponible pour le moment", actionsCount());
            return;
        }

        IAction action = nextAction.get();
        log.info("Execution de l'action {}", action.name());
        action.execute();

        if (action.isCompleted()) {
            log.info("L'action {} est terminé.", action.name());
            actions.remove(action);
            completedActions.add(action);
        }

        log.info("Il reste {} actions disponible", actionsCount());
    }

    public int actionsCount() {
        return actions.size();
    }
}
