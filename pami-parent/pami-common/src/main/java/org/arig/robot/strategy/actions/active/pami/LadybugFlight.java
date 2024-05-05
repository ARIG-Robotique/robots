package org.arig.robot.strategy.actions.active.pami;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;

import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Point;
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
    protected TableUtils tableUtils;

    @Autowired
    protected EurobotStatus rs;

    @Autowired
    protected TrajectoryManager mv;

    @Autowired
    protected PamiRobotServosService servos;

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
        return new Point(getX(145), 1475);
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.getRemainingTime() < 10000;
    }

    @Override
    public void execute() {
        try {
            mv.setVitessePercent(100, 100);
            mv.avanceMM(100);
            mv.pathTo(entryPoint(), GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 30 : 150);
            rs.enableCalageBordure(TypeCalage.FORCE);
            mv.avanceMM(100);

        } catch (AvoidingException | NoPathFoundException e) {
            log.error("AvoidingException", e);
        } finally {
           complete(true);
        }
    }
}
