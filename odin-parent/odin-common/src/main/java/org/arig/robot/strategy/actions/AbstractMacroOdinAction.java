package org.arig.robot.strategy.actions;

import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.strategy.AbstractMacroAction;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractMacroOdinAction extends AbstractMacroAction {

    @Autowired
    protected OdinRobotStatus rs;

}
