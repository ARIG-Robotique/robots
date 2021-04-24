package org.arig.robot.strategy.actions;

import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNerellAction extends AbstractAction {

    @Autowired
    protected NerellRobotStatus rs;

    @Autowired
    protected ServosService servos;

}
