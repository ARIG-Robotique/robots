package org.arig.robot.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ordonancement des actions a réaliser.
 *
 * @author gdepuille on 06/05/15.
 */
@Slf4j
public class StrategyManager {

    @Autowired
    private List<IAction> actions;

    public void execute() {
        log.info("Recherche d'une statégie à éxécuter parmis les {} actions disponible", actionsCount());

        // On recherche la stratégy adapté.
        List<IAction> filteredActions = actions.stream()
                .filter(IAction::isValid)
                .sorted(Comparator.comparingInt(IAction::order).reversed())
                .limit(1).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(filteredActions)) {
            log.warn("Plus d'action disponible");
            return;
        }

        IAction action = filteredActions.get(0);
        log.info("Execution de l'action {}", action.name());
        action.execute();

        if (action.isCompleted()) {
            log.info("L'action {} est terminé. On la supprime de la liste.", action.name());
            actions.remove(action);
        }

        log.info("Il reste {} actions disponible", actionsCount());
    }

    public int actionsCount() {
        return actions.size();
    }
}
