package org.arig.robot.strategy.actions;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

    protected void checkRecalageXmm(double realXmm) {
        final double robotX = position.getPt().getX();
        final double realX = conv.mmToPulse(realXmm);
        if (Math.abs(realX - robotX) > conv.mmToPulse(10)) {
            log.warn("RECALAGE X REQUIS (diff > 10 mm) : xRobot = {} mm ; xReel = {} mm",
                    conv.pulseToMm(robotX), realXmm);
            position.getPt().setX(realX);
        } else {
            log.info("Recalage X inutile : xRobot = {} mm ; xReel = {} mm",
                    conv.pulseToMm(robotX), realXmm);
        }
    }

    protected void checkRecalageYmm(double realYmm) {
        final double robotY = position.getPt().getY();
        final double realY = conv.mmToPulse(realYmm);
        if (Math.abs(realY - robotY) > conv.mmToPulse(10)) {
            log.warn("RECALAGE Y REQUIS (diff > 10 mm) : yRobot = {} mm ; yReel = {} mm",
                    conv.pulseToMm(robotY), realYmm);
            position.getPt().setY(realY);
        } else {
            log.info("Recalage Y inutile : yRobot = {} mm ; yReel = {} mm",
                    conv.pulseToMm(robotY), realYmm);
        }
    }

    protected void checkRecalageAngleDeg(double realAdeg) {
        final double robotA = position.getAngle();
        final double realA = conv.degToPulse(realAdeg);
        if (Math.abs(realA - robotA) > conv.degToPulse(2)) {
            log.warn("RECALAGE ANGLE REQUIS (> 2°) : aRobot = {} ; aReel = {}",
                    conv.pulseToDeg(robotA), realAdeg);
            position.setAngle(realA);
        } else {
            log.info("Recalage angle inutile : aRobot = {} ; aReel = {}",
                    conv.pulseToDeg(robotA), realAdeg);
        }
    }
}
