package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Phare extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition;

    @Autowired
    private ServosService servos;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Phare";
    }

    @Override
    public int order() {
        int order = 13;
        return order + rs.getDistanceParcours();
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.isPhare();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            double y = 1780;
            double x = 215;
            if (rs.getTeam() == ETeam.BLEU) {
                mv.pathTo(x, y);
            } else {
                mv.pathTo(3000 - x, y);
            }

            if (conv.pulseToDeg(currentPosition.getAngle()) <= Math.abs(90)) {
                // On leve avec le bras droit
                servos.brasDroitPhare(false);
            } else {
                // On leve avec le bras gauche
                servos.brasGauchePhare(false);
            }

            if (rs.getTeam() == ETeam.BLEU) {
                mv.gotoPointMM(370, y, true);
            } else {
                mv.gotoPointMM(3000 - 370, y, true);
            }
            rs.setPhare(true);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = rs.isPhare();
            servos.brasDroitFerme(false);
            servos.brasGaucheFerme(false);
        }
    }
}
