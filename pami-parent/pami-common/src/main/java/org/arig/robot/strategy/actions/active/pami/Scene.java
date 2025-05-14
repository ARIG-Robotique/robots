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
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.utils.TableUtils;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Scene extends AbstractEurobotAction {

    @Autowired
    private TableUtils tableUtils;

    @Autowired
    private EurobotStatus rs;

    @Autowired
    private TrajectoryManager mv;

    @Autowired
    private RobotName robotName;

    @Getter
    private final boolean completed = false;

    public int executionTimeMs() {
        return 0;
    }

    @Override
    public String name() {
        return "Scene";
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(1270), 1890);
    }

    @Override
    public void refreshCompleted() {
        if (robotName.id() != RobotName.RobotIdentification.PAMI_STAR) {
            log.info("Scene que pour la Superstar");
            complete(true);
        }
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    public boolean isValid() {
        if (robotName.id() != RobotName.RobotIdentification.PAMI_STAR) {
            return false;
        }
        return isTimeValid() && rs.getRemainingTime() <= EurobotConfig.pamiStartRemainingTimeMs;
    }

    @Override
    public void execute() {
        try {
            mv.setVitessePercent(100, 100);
            Point entry = entryPoint();
            mv.pathTo(entry, GotoOption.AVANT);
            mv.gotoPoint(entry.getX(), 1700, GotoOption.AVANT);
            mv.setVitessePercent(20, 100);
            rs.enableCalage(TypeCalage.PRISE_PRODUIT_SOL_AVANT);
            mv.avanceMM(300);

            complete(true);
            rs.disableAvoidance();

            ThreadUtils.sleep((int) rs.getRemainingTime());
        } catch (AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'accès a la scene", e);
        }
    }
}
