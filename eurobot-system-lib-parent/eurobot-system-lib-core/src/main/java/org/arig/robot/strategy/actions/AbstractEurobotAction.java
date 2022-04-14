package org.arig.robot.strategy.actions;

import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.RobotName.RobotIdentification;
import org.arig.robot.model.Team;
import org.arig.robot.services.AbstractCommonServosService;
import org.arig.robot.services.CommonIOService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractEurobotAction extends AbstractAction {

    @Autowired
    protected RobotConfig robotConfig;

    @Autowired
    protected RobotName robotName;

    @Autowired
    protected AbstractCommonServosService commonServosService;

    @Autowired
    protected CommonIOService commonIOService;

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
    protected RobotGroupService group;

    @Autowired
    protected EurobotStatus rs;

    protected int getX(int x) {
        return tableUtils.getX(rs.team() == Team.VIOLET, x);
    }

    protected boolean remainingTimeValid() {
        int time = robotName.id() == RobotIdentification.NERELL ? EurobotConfig.validRetourSiteDeFouilleRemainingTimeNerell : EurobotConfig.validRetourSiteDeFouilleRemainingTimeOdin;
        return rs.getRemainingTime() > time;
    }
}
