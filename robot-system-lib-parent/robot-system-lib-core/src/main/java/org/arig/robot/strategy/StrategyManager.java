package org.arig.robot.strategy;

import lombok.Getter;
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

    private final String ACTION_EMPTY = "Aucune";

    @Getter
    @Autowired
    private List<IAction> actions;

    @Getter
    private String currentAction = ACTION_EMPTY;

    private List<IAction> completedActions = new ArrayList<>();

    public void execute() {
        if (currentAction != ACTION_EMPTY) {
            log.info("Recherche d'une statégie à éxécuter parmis les {} actions disponible", actionsCount());
        }

        // On recherche la stratégy adapté.
        Optional<IAction> nextAction = actions.stream()
                .filter(IAction::isValid)
                .sorted(Comparator.comparingInt(IAction::order).reversed())
                .findFirst();

        if (!nextAction.isPresent()) {
            if (currentAction != ACTION_EMPTY) {
                log.warn("0/{} actions disponible pour le moment", actionsCount());
            }
            currentAction = ACTION_EMPTY;
            return;
        }

        IAction action = nextAction.get();
        currentAction = action.name();
        log.info("Execution de l'action {}", currentAction);
        action.execute();

        if (action.isCompleted()) {
            log.info("L'action {} est terminé.", currentAction);
            actions.remove(action);
            completedActions.add(action);
        }

        log.info("Il reste {} actions disponible", actionsCount());
    }

    public int actionsCount() {
        return actions.size();
    }
}
