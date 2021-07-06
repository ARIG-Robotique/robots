package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinPhare extends AbstractOdinAction {

    public static final double ENTRY_X = 230;
    public static final double ENTRY_Y = 1820;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PHARE;
    }

    @Override
    public Point entryPoint() {
        double x = ENTRY_X;
        double y = ENTRY_Y;
        if (ETeam.JAUNE == rsOdin.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = 13;
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        if (rs.twoRobots() && !rsOdin.groupOk()) {
            return false;
        }

        return isTimeValid() && !rsOdin.phare() && !rsOdin.inPort();
    }

    @Override
    public void refreshCompleted() {
        if (rsOdin.phare()) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();
            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            final double angleRobot = conv.pulseToDeg(position.getAngle());
            final SensRotation sensRotation;
            if (Math.abs(angleRobot) <= 90) {

                // On active avec le bras gauche
                servosOdin.brasGauchePhare(true);
                sensRotation = angleRobot < 0 ? SensRotation.TRIGO : SensRotation.HORAIRE;
                mv.gotoOrientationDegSansDistance(45, sensRotation);

            } else {
                // On active avec le bras droit
                servosOdin.brasDroitPhare(true);
                sensRotation = angleRobot > 0 ? SensRotation.TRIGO : SensRotation.HORAIRE;
                mv.gotoOrientationDegSansDistance(-180 + 45, sensRotation);
            }
            group.phare();

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            complete();
            servosOdin.brasDroitFerme(false);
            servosOdin.brasGaucheFerme(false);
        }
    }
}
