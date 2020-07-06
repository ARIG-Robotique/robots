package org.arig.robot.strategy.actions;

import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractNerellAction extends AbstractAction {

    @Autowired
    protected ITrajectoryManager mv;

    @Autowired
    protected NerellRobotStatus rs;

    @Autowired
    protected ServosService servos;

    @Autowired
    protected ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    protected Position position;

    @Autowired
    protected TableUtils tableUtils;

}
