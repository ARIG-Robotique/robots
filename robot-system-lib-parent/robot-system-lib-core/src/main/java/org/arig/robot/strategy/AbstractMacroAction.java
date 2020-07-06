package org.arig.robot.strategy;

import org.arig.robot.model.Point;

import java.util.List;

public abstract class AbstractMacroAction extends AbstractAction {

    protected List<AbstractAction> actions;

    @Override
    public Point entryPoint() {
        return actions.get(0).entryPoint();
    }

    @Override
    public boolean isValid() {
        return actions.stream().allMatch(AbstractAction::isValid);
    }

    @Override
    public void execute() {
        for (AbstractAction action : actions) {
            action.execute();

            // FIXME détection d'action annullée
            if (!action.isCompleted() && !action.isTimeValid()) {
                break;
            }
        }

        complete();
    }

}
