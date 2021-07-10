package org.arig.robot.strategy.actions;

import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.NerellServosService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNerellAction extends AbstractEurobotAction {

    @Autowired
    protected NerellRobotStatus rsNerell;

    @Autowired
    protected NerellServosService servosNerell;

    @Autowired
    protected INerellIOService ioService;

}
