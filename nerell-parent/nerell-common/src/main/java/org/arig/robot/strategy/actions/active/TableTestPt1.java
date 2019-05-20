package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TableTestPt1 extends AbstractAction {

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private RobotStatus rs;

    private int order = 1000;

    @Override
    public String name() {
        return "Point 1 table test";
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            trajectoryManager.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            trajectoryManager.pathTo(560, 400);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            order--;
        }
    }
}
