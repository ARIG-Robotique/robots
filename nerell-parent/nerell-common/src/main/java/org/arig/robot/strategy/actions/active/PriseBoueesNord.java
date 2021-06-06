package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ENerellStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseBoueesNord extends AbstractNerellAction {

    private boolean firstExecution = true;

    @Override
    public String name() {
        return "Prise bouées nord";
    }

    @Override
    public Point entryPoint() {
        double x = 225;
        double y = 1200;
        if (ETeam.JAUNE == rs.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        if (rs.strategy() == ENerellStrategy.BASIC_NORD && firstExecution) {
            return 1000;
        }

        return 6 + (rs.ecueilCommunEquipePris() ? 0 : 10) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return rs.pincesAvantEmpty() &&
                rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime &&
                (rs.team() == ETeam.BLEU && rs.grandChenalVertEmpty() || rs.grandChenalRougeEmpty());
    }

    @Override
    public void execute() {
        firstExecution = false;
        try {
            rs.enablePincesAvant();

            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            if (rs.strategy() != ENerellStrategy.BASIC_NORD && tableUtils.distance(entry) > 100) {
                mv.pathTo(entry);
            } else {
                // Le path active l'évittement en auto, pas de path, pas d'evittement
                rs.enableAvoidance();
            }

            double targetx = 434;
            double targety = 1200 + 570;
            if (ETeam.JAUNE == rs.team()) {
                targetx = 3000 - targetx;
            }
            final Point target = new Point(targetx, targety);

            if (rs.team() == ETeam.BLEU) {
                if (rs.strategy() != ENerellStrategy.BASIC_NORD) {
                    mv.gotoPoint(220, 1290);
                    mv.gotoOrientationDeg(66);
                }

                mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                rs.boueePrise(1);
                rs.boueePrise(2);

                mv.gotoOrientationDeg(0);
                mv.gotoPoint(640, targety, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                rs.boueePrise(5);

                mv.gotoPoint(940, 1662, GotoOption.AVANT);
                rs.boueePrise(6);

            } else {
                if (rs.strategy() != ENerellStrategy.BASIC_NORD) {
                    mv.gotoPoint(3000 - 220, 1290);
                    mv.gotoOrientationDeg(180 - 66);
                }

                mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                rs.boueePrise(13);
                rs.boueePrise(14);

                mv.gotoOrientationDeg(180);
                mv.gotoPoint(3000 - 640, targety, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                rs.boueePrise(12);

                mv.gotoPoint(3000 - 940, 1662, GotoOption.AVANT);
                rs.boueePrise(11);
            }

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            complete();
        }
    }
}
