package org.arig.test.robot.strategy.actions;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.AbstractAction;

/**
 * @author gdepuille on 06/05/15.
 */
@Slf4j
public class OneShotAction extends AbstractAction {

    @Override
    public String name() {
        return "OneShotAction Test";
    }

    @Override
    public Point entryPoint() {
        return null;
    }

    @Override
    public int order() {
        return 12;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public void execute() {
        log.info("One Shot action test (ordre {})", order());
    }
}
