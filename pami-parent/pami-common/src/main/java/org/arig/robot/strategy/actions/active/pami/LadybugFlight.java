package org.arig.robot.strategy.actions.active.pami;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;

import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.PamiRobotServosService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LadybugFlight extends AbstractAction {

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

    protected int getX(int x) {
        return tableUtils.getX(rs.team() == Team.JAUNE, x);
    }

    @Override
    public String name() {
        return "Ladybug flight";
    }

    @Override
    public Point entryPoint() {
        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            return new Point(getX(145), 455);
        } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
            return new Point(getX(145), 1475);
        }
        // ROND
        return new Point(getX(830), 1885);
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.getRemainingTime() <= EurobotConfig.pamiStartRemainingTimeMs;
    }

    @Override
    public void execute() {
        try {
            mv.setVitessePercent(100, 100);
            rs.disableAvoidance();
            if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
                mv.avanceMM(350);
                mv.pathTo(entryPoint(), GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
                mv.gotoOrientationDeg(rs.team() == Team.BLEU ? -150 : -30);

            } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
                mv.avanceMM(200);
                mv.pathTo(entryPoint(), GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
                mv.gotoOrientationDeg(rs.team() == Team.BLEU ? 150 : 30);

            } else {
                mv.avanceMM(50);
                mv.gotoPoint(entryPoint());
                mv.gotoOrientationDeg(rs.team() == Team.BLEU ? 150 : 30);

            }

            rs.enableCalageBordure(TypeCalage.FORCE);
            mv.avanceMM(300);
            rs.disableAsserv();

        } catch (AvoidingException | NoPathFoundException e) {
            log.error("AvoidingException", e);
        } finally {
           complete(true);
        }
    }
}
