package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.communication.balise.enums.DirectionGirouette;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetourAuPort extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Retour au port";
    }

    @Override
    public int order() {
        int order;
        if (rs.getDirectionGirouette() == DirectionGirouette.UNKNOWN) {
            order = 5;
        } else {
            order = 10;
        }

        return order + rs.getDistanceParcours();
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.getRemainingTime() < 30000;
    }

    @Override
    public void execute() {
        boolean coordProjection = false;
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            double x = 460;
            double y = rs.getDirectionGirouette() == DirectionGirouette.UP ? 1770 : 625;
            if (rs.getTeam() == ETeam.BLEU) {
                mv.pathTo(x, y);
            } else {
                mv.pathTo(3000 - x, y);
            }
            setScore(coordProjection = true);

            x = 215;
            y = rs.getDirectionGirouette() == DirectionGirouette.UP ? 1770 : 625;
            if (rs.getTeam() == ETeam.BLEU) {
                mv.pathTo(x, y);
            } else {
                mv.pathTo(3000 - x, y);
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = true;
            setScore(coordProjection);
        }
    }

    private void setScore(boolean coordProjection) {
        if (coordProjection && rs.getDirectionGirouette() != DirectionGirouette.UNKNOWN) {
            rs.setBonPort(true);
        } else if (coordProjection) {
            rs.setMauvaisPort(true);
        }
    }
}
