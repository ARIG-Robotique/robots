package org.arig.robot.strategy.actions;

import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.strategy.AbstractMacroAction;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractMacroNerellAction extends AbstractMacroAction {

    @Autowired
    protected NerellRobotStatus rs;

}
