package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummyAction extends AbstractAction {

    @Getter
    private boolean completed = false;

    @Getter
    private boolean valid = true;

    @Override
    public String name() {
        return "Dummy";
    }

    @Override
    public int order() {
        return -1;
    }

    @Override
    public void execute() {
        ThreadUtils.sleep(1000);
    }
}
