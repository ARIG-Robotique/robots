package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gdepuille on 06/11/16.
 */
@Slf4j
@Component
public class DuyAction extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false; // Jamais terminé quoi qu'il se passe

    @Override
    public String name() {
        return "Duy Action";
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public boolean isValid() {
        return isTimeValid();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.gotoPointMM(2000, 600, false, false);
            mv.pathTo(2500, 1100);
            mv.gotoPointMM(1000, 600, true, false);

            completed = true;
        } catch (RefreshPathFindingException | NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
