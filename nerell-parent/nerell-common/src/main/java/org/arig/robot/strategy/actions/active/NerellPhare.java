package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellPhare extends AbstractNerellAction {

    public static final double ENTRY_X = 225;
    public static final double ENTRY_Y = 1760;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PHARE;
    }

    @Override
    public Point entryPoint() {
        double x = ENTRY_X;
        double y = ENTRY_Y;
        if (ETeam.JAUNE == rsNerell.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = 13;

        if (rsNerell.ecueilCommunEquipePris()) {
            order += 30;
        }

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsNerell.phare() && !rsNerell.inPort();
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();
            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            final double angleRobot = conv.pulseToDeg(position.getAngle());
            if (Math.abs(angleRobot) <= 90) {
                if (angleRobot < 0) {
                    mv.gotoOrientationDegSansDistance(0);
                }

                // On active avec le bras gauche
                servosNerell.brasGauchePhare(true);
                mv.gotoOrientationDegSansDistance(-35, SensRotation.HORAIRE);

            } else {
                if (angleRobot < 0) {
                    mv.gotoOrientationDegSansDistance(180);
                }

                // On active avec le bras droit
                servosNerell.brasDroitPhare(true);
                mv.gotoOrientationDegSansDistance(-180 + 35, SensRotation.TRIGO);
            }
            group.phare();

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            complete();
            servosNerell.brasDroitFerme(false);
            servosNerell.brasGaucheFerme(false);
        }
    }
}
