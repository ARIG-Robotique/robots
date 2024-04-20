package org.arig.robot.strategy.actions;

import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellRobotServosService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNerellAction extends AbstractEurobotAction {

    @Autowired
    protected NerellRobotStatus rsNerell;

    @Autowired
    protected NerellRobotServosService servosNerell;

    @Autowired
    protected NerellIOService ioService;

    @Autowired
    protected BrasService bras;

}
