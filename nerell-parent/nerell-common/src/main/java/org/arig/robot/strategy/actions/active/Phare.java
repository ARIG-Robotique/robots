package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Phare extends AbstractNerellAction {

    public static final double ENTRY_X = 225;
    public static final double ENTRY_Y = 1760;

    @Override
    public String name() {
        return "Phare";
    }

    @Override
    public Point entryPoint() {
        double x = ENTRY_X;
        double y = ENTRY_Y;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = 13;

        if (rs.isEcueilCommunEquipePris()) {
            order += 30;
        }

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.phare() && !rs.inPort();
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.pathTo(entry);

            final double angleRobot = conv.pulseToDeg(position.getAngle());
            if (Math.abs(angleRobot) <= 90) {
                if (angleRobot < 0) {
                    mv.gotoOrientationDegSansDistance(0);
                }

                // On active avec le bras gauche
                servos.brasGauchePhare(true);
                mv.gotoOrientationDegSansDistance(-35, SensRotation.HORAIRE);

            } else {
                if (angleRobot < 0) {
                    mv.gotoOrientationDegSansDistance(180);
                }

                // On active avec le bras droit
                servos.brasDroitPhare(true);
                mv.gotoOrientationDegSansDistance(-180 + 35, SensRotation.TRIGO);
            }
            rs.phare(true);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            complete();
            servos.brasDroitFerme(false);
            servos.brasGaucheFerme(false);
        }
    }
}
