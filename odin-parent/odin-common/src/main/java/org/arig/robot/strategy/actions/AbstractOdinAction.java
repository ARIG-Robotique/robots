package org.arig.robot.strategy.actions;

import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.services.OdinServosService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractOdinAction extends AbstractAction {

    @Autowired
    protected RobotConfig robotConfig;

    @Autowired
    protected TrajectoryManager mv;

    @Autowired
    protected ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    protected Position position;

    @Autowired
    protected TableUtils tableUtils;

    @Autowired
    protected OdinRobotStatus rs;

    @Autowired
    protected OdinServosService servos;

    @Autowired
    protected RobotGroupService group;

}
