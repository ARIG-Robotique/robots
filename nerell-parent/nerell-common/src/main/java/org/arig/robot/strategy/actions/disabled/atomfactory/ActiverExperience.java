package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActiverExperience extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servos;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Activation de l'expérience";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        try {
            rs.disableAvoidance();

            // le robot est déjà en position face à l'expérience
            mv.avanceMM(200); // TODO à définir

            completed = true;
            rs.setExperienceActivee(true);

            mv.reculeMM(100); // TODO à définir

            if (rs.getTeam().equals(Team.VIOLET)) {
                mv.gotoOrientationDeg(-135);
            } else {
                mv.gotoOrientationDeg(-45);
            }

        } catch (RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
