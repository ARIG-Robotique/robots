package org.arig.robot.strategy;

import org.arig.robot.model.Point;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class AbstractMacroAction extends AbstractAction {

    @Autowired
    protected TableUtils tableUtils;

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
