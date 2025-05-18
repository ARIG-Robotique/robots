package org.arig.robot.strategy.actions.active.pami;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.utils.TableUtils;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MoshPit extends AbstractEurobotAction {

    private int tryFinalPoint = 0;
    private boolean firstTime = true;

    public int executionTimeMs() {
        return 0;
    }

    @Override
    public String name() {
        return "Mosh pit";
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(600), 1625);
    }

    @Override
    public void refreshCompleted() {
        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            log.info("Mosh pit pas pour la Superstar");
            complete(true);
        }
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    public boolean isValid() {
        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            return false;
        }
        return isTimeValid() && rs.getRemainingTime() <= EurobotConfig.pamiStartRemainingTimeMs;
    }

    @Override
    public void execute() {
        try {
            mv.setVitessePercent(100, 100);
            if (firstTime && robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
                ThreadUtils.sleep(3000);
            }
            mv.gotoPoint(entryPoint(), GotoOption.AVANT);
            if (robotName.id() == RobotName.RobotIdentification.PAMI_ROND) {
                if (tryFinalPoint == 0) {
                    mv.pathTo(getX(1500), 1280);
                } else if (tryFinalPoint == 1) {
                    mv.pathTo(getX(1275), 1320);
                } else if (tryFinalPoint == 2) {
                    mv.pathTo(getX(1790), 1320);
                }
            } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
                mv.pathTo(getX(830), 1520);
            }
            mv.alignFrontTo(getX(1270), 1600);
            complete(true);
            rs.disableAvoidance();

            ThreadUtils.sleep((int) rs.getRemainingTime());
        } catch (AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'accès au mosh pit", e);
        } finally {
            firstTime = false;
            tryFinalPoint++;
            if (tryFinalPoint > 2) {
                tryFinalPoint = 0;
            }
        }
    }
}
