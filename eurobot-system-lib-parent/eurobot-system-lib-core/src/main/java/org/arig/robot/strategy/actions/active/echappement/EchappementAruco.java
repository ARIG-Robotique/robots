package org.arig.robot.strategy.actions.active.echappement;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EchappementAruco extends AbstractEurobotAction {

    @Getter
    private final boolean completed = false;

    @Override
    public String name() {
        return EurobotConfig.ACTION_ECHAPPEMENT_ARUCO_PREFIX + robotName.id();
    }

    @Override
    public int executionTimeMs() {
        return 4000;
    }

    @Override
    public Point entryPoint() {
        if (robotName.id() == RobotName.RobotIdentification.NERELL) {
            return new Point(getX(1500), 550);
        } else {
            return new Point(getX(1500), 950);
        }
    }

    @Override
    public int order() {
        return -110;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && timeBeforeRetourValid();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(config.vitesse(80), config.vitesseOrientation());
            mv.pathTo(entryPoint());

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'éxecution de l'action : {}", e.getMessage());
        }
    }
}
