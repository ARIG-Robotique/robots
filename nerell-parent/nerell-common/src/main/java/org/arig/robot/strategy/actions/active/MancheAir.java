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
public class MancheAir extends AbstractAction {

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
        return "Manche à Air";
    }

    @Override
    public int order() {
        int order = 0;
        if (!rs.isMancheAAir1() && !rs.isMancheAAir2()) {
            order += 15;
        } else {
            order += 5;
        }
        return order + rs.getDistanceParcours();
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && (!rs.isMancheAAir1() || !rs.isMancheAAir2());
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            double y = 225;
            if (!rs.isMancheAAir1()) {
                double x = 215;
                if (rs.getTeam() == ETeam.BLEU) {
                    mv.pathTo(x, y);
                } else {
                    mv.pathTo(3000 - x, y);
                }

                if (conv.pulseToDeg(currentPosition.getAngle()) <= Math.abs(90)) {
                    // On leve avec le bras gauche
                    servos.brasGaucheMancheAAir(false);
                } else {
                    // On leve avec le bras droit
                    servos.brasDroitMancheAAir(false);
                }

                if (rs.getTeam() == ETeam.BLEU) {
                    mv.gotoPointMM(300, y, true);
                } else {
                    mv.gotoPointMM(3000 - 300, y, true);
                }
                rs.setMancheAAir1(true);
            }

            if (!rs.isMancheAAir2()) {
                double x = 600;
                if (rs.getTeam() == ETeam.BLEU) {
                    mv.pathTo(x, y);
                } else {
                    mv.pathTo(3000 - x, y);
                }

                if (conv.pulseToDeg(currentPosition.getAngle()) <= Math.abs(90)) {
                    // On leve avec le bras gauche
                    servos.brasGaucheMancheAAir(false);
                } else {
                    // On leve avec le bras droit
                    servos.brasDroitMancheAAir(false);
                }

                if (rs.getTeam() == ETeam.BLEU) {
                    mv.gotoPointMM(675, y, true);
                } else {
                    mv.gotoPointMM(3000 - 675, y, true);
                }
                rs.setMancheAAir2(true);
            }
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = rs.isMancheAAir1() && rs.isMancheAAir2();
            servos.brasDroitFerme(false);
            servos.brasGaucheFerme(false);
        }
    }
}
