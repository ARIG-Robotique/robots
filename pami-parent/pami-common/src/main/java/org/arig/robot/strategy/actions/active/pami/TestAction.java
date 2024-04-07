package org.arig.robot.strategy.actions.active.pami;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestAction extends AbstractAction {

    @Getter
    private final boolean completed = false;

    @Override
    public String name() {
        return "Test";
    }

    @Override
    public Point entryPoint() {
        return new Point(1500, 950);
    }

    @Override
    public int order() {
        return -100;
    }

    @Override
    public boolean isValid() {
        return isTimeValid();
    }

    @Override
    public void execute() {

    }
}
