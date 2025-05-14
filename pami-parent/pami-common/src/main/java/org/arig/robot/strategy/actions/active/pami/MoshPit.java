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

    public int executionTimeMs() {
        return 0;
    }

    @Override
    public String name() {
        return "Mosh pit";
    }

    @Override
    public Point entryPoint() {
        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            return new Point(getX(1920), 1420);
        } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
            return new Point(getX(1500), 1390);
        } else {
            return new Point(getX(1000), 1420);
        }
    }

    @Override
    public void refreshCompleted() {
        if (robotName.id() == RobotName.RobotIdentification.PAMI_STAR) {
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
        if (robotName.id() == RobotName.RobotIdentification.PAMI_STAR) {
            return false;
        }
        return isTimeValid() && rs.getRemainingTime() <= EurobotConfig.pamiStartRemainingTimeMs;
    }

    @Override
    public void execute() {
        try {
            mv.setVitessePercent(100, 100);
            mv.pathTo(entryPoint(), GotoOption.AVANT);
            mv.alignFrontTo(getX(1270), 1600);
            complete(true);
            rs.disableAvoidance();

            ThreadUtils.sleep((int) rs.getRemainingTime());
        } catch (AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'accès au mosh pit", e);
        }
    }
}
