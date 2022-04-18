package org.arig.robot.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.services.LidarService;
import org.arig.robot.system.group.RobotGroup;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Ordonancement des actions a réaliser.
 */
@Slf4j
public class StrategyManager {

    @Autowired
    private List<Action> actions;

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    private TableUtils tableUtils;

    @Autowired
    private RobotGroup group;

    @Autowired
    private LidarService lidarService;

    public synchronized List<Action> actions() {
        return actions;
    }

    public void execute() {
        if (rs.currentAction() != null) {
            log.info("Recherche d'une action à exécuter parmis les {} disponible(s)", actionsCount());
        }

        final String otherCurrentAction = rs.otherCurrentAction();

        Optional<Action> nextAction = actions().stream()
                .filter(Action::isValid)
                .filter(a -> !StringUtils.equals(otherCurrentAction, a.name()))
                .filter(a -> !a.blockingActions().contains(otherCurrentAction))
                .filter(a -> !lidarService.hasObstacleInZone(a.blockingZone()))
                .sorted(Comparator.comparingInt(Action::order).reversed())
                .findFirst();

        if (!nextAction.isPresent()) {
            if (rs.currentAction() != null) {
                log.warn("0/{} actions disponible pour le moment", actionsCount());
            }
            group.setCurrentAction(null);
            return;
        }

        final Action action = nextAction.get();
        group.setCurrentAction(action.name());
        log.info("Execution de l'action {}", action.name());
        tableUtils.clearDynamicDeadZones();

        try {
            action.execute();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            actions().remove(action);
        }

        if (action.isCompleted()) {
            log.info("L'action {} est terminé.", action.name());
        }

        // Purge des actions terminé par l'autre robot entre temps
        log.info("Purge des actions terminés entre temps");
        final List<Action> completedActions = actions().stream()
                .peek(Action::refreshCompleted)
                .filter(Action::isCompleted)
                .collect(Collectors.toList());
        completedActions.forEach(a -> actions().remove(a));

        log.info("Il reste {} actions disponibles", actionsCount());
    }

    public int actionsCount() {
        return actions().size();
    }
}
