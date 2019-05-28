package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EchappementAction extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Echappement target centre table";
    }

    @Override
    public int order() {
        return -1;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            if (rs.getTeam() == Team.JAUNE) {
                mv.gotoPointMM(250, 1500);
            } else {
                mv.gotoPointMM(2750, 1500);
            }

            return;
        } catch (AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {} pour aller au centre de la table", e.toString());
        }

        try {
            rs.disableAvoidance();
            mv.tourneDeg(180);
        } catch (RefreshPathFindingException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {} pour faire demi tour", e.toString());
        }
    }
}
