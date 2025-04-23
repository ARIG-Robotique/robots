package org.arig.robot.strategy.actions;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.MatchDoneException;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.RobotName.RobotIdentification;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.AbstractCommonRobotServosService;
import org.arig.robot.services.CommonRobotIOService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractEurobotAction extends AbstractAction {

    @Autowired
    protected RobotConfig config;

    @Autowired
    protected RobotName robotName;

    @Autowired
    protected AbstractCommonRobotServosService servos;

    @Autowired
    protected CommonRobotIOService io;

    @Autowired
    protected TrajectoryManager mv;

    @Autowired
    protected TableUtils tableUtils;

    @Autowired
    protected List<RobotGroupService> groups;

    @Autowired
    protected EurobotStatus rs;

    @Autowired
    protected ThreadPoolExecutor executor;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    protected int getX(int x) {
        return tableUtils.getX(rs.team() == Team.BLEU, x);
    }

    public abstract int executionTimeMs();

    @Override
    protected boolean isTimeValid() {
        boolean timeValid = super.isTimeValid();
        if (!timeValid) {
            return false;
        }

        // Check remaining time with the time need by the action
        if (rs.getRemainingTime() < executionTimeMs()) {
            return false;
        }

        // Everything is OK
        return true;
    }

    protected boolean ilEstTempsDeRentrer() {
        if (robotName.id() == RobotIdentification.NERELL) {
            return rs.getRemainingTime() < EurobotConfig.validRetourBackstageRemainingTimeNerell;
        }

        return false;
    }

    public void checkMatchDone() throws MatchDoneException {
        if (!rs.matchRunning()) {
            throw new MatchDoneException();
        }
    }

    private boolean isCalage(TypeCalage ... calages) {
        boolean calageDone = false;
        for (TypeCalage calage : calages) {
            if (rs.calageCompleted().contains(calage)) {
                calageDone = true;
                break;
            }
        }
        return calageDone;
    }

    protected void checkRecalageXmm(double realXmm, TypeCalage ... calages) {
        if (!isCalage(calages)) {
            log.warn("Recalage Xmm {} not valid, calage not done", realXmm);
            return;
        }

        final double robotX = mv.currentXMm();
        if (Math.abs(realXmm - robotX) > 10) {
            log.warn("RECALAGE X REQUIS (diff > 10 mm) : xRobot = {} mm ; xReel = {} mm", robotX, realXmm);
            position.getPt().setX(conv.mmToPulse(realXmm));
        } else {
            log.info("Recalage X inutile : xRobot = {} mm ; xReel = {} mm", robotX, realXmm);
        }
    }

    protected void checkRecalageYmm(double realYmm, TypeCalage ... calages) {
        if (!isCalage(calages)) {
            log.warn("Recalage Ymm {} not valid, calage not done", realYmm);
            return;
        }

        final double robotY = mv.currentYMm();
        if (Math.abs(realYmm - robotY) > 10) {
            log.warn("RECALAGE Y REQUIS (diff > 10 mm) : yRobot = {} mm ; yReel = {} mm", robotY, realYmm);
            position.getPt().setY(conv.mmToPulse(realYmm));
        } else {
            log.info("Recalage Y inutile : yRobot = {} mm ; yReel = {} mm", robotY, realYmm);
        }
    }

    protected void checkRecalageAngleDeg(double realAdeg, TypeCalage ... calages) {
        if (!isCalage(calages)) {
            log.warn("Recalage angle deg {} not valid, calage not done", realAdeg);
            return;
        }

        final double robotA = mv.currentAngleDeg();
        if (Math.abs(realAdeg - robotA) > 2) {
            log.warn("RECALAGE ANGLE REQUIS (> 2°) : aRobot = {} ; aReel = {}", robotA, realAdeg);
            position.setAngle(conv.degToPulse(realAdeg));
        } else {
            log.info("Recalage angle inutile : aRobot = {} ; aReel = {}", robotA, realAdeg);
        }
    }

    protected CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executor);
    }

    protected <T> CompletableFuture<T> supplyAsync(Supplier<T> runnable) {
        return CompletableFuture.supplyAsync(runnable, executor);
    }
}
