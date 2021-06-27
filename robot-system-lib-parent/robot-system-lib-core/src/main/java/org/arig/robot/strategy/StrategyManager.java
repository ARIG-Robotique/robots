package org.arig.robot.strategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.services.LidarService;
import org.arig.robot.system.group.IRobotGroup;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Ordonancement des actions a réaliser.
 */
@Slf4j
public class StrategyManager {

    @Getter
    @Autowired
    private List<IAction> actions;

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    private TableUtils tableUtils;

    @Autowired
    private IRobotGroup group;

    @Autowired
    private LidarService lidarService;

    private List<IAction> completedActions = new ArrayList<>();

    public void execute() {
        if (rs.currentAction() != null) {
            log.info("Recherche d'une action à exécuter parmis les {} disponible(s)", actionsCount());
        }

        final String otherCurrentAction = group.getCurrentAction();

        Optional<IAction> nextAction = actions.stream()
                .filter(IAction::isValid)
                .filter(a -> !StringUtils.equals(otherCurrentAction, a.name()))
                .filter(a -> !a.blockingActions().contains(otherCurrentAction))
                .filter(a -> lidarService.hasObstacleInZone(a.blockingZone()))
                .sorted(Comparator.comparingInt(IAction::order).reversed())
                .findFirst();

        if (!nextAction.isPresent()) {
            if (rs.currentAction() != null) {
                log.warn("0/{} actions disponible pour le moment", actionsCount());
            }
            rs.currentAction(null);
            return;
        }

        final IAction action = nextAction.get();
        rs.currentAction(action.name());
        log.info("Execution de l'action {}", action.name());
        tableUtils.clearDynamicDeadZones();
        action.execute();

        if (action.isCompleted()) {
            log.info("L'action {} est terminé.", action.name());
            actions.remove(action);
            completedActions.add(action);
        }

        log.info("Il reste {} actions disponibles", actionsCount());
    }

    public int actionsCount() {
        return actions.size();
    }
}
