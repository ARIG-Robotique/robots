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
public class NerellPriseBoueesNord extends AbstractNerellAction {

    private boolean firstExecution = true;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_BOUEE_NORD;
    }

    @Override
    public Point entryPoint() {
        double x = 225;
        double y = 1200;
        if (ETeam.JAUNE == rsNerell.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        if (rsNerell.strategy() == ENerellStrategy.BASIC_NORD && firstExecution) {
            return 1000;
        }

        return 6 + (rsNerell.ecueilCommunEquipePris() ? 0 : 10) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return rsNerell.pincesAvantEmpty() &&
                rsNerell.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime &&
                (rsNerell.team() == ETeam.BLEU && rsNerell.grandChenalVertEmpty() || rsNerell.grandChenalRougeEmpty());
    }

    @Override
    public void execute() {
        firstExecution = false;
        try {
            rsNerell.enablePincesAvant();

            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            if (rsNerell.strategy() != ENerellStrategy.BASIC_NORD && tableUtils.distance(entry) > 100) {
                mv.pathTo(entry);
            } else {
                // Le path active l'évitement en auto, pas de path, pas d'évitement
                rsNerell.enableAvoidance();
            }

            double targetx = 436;
            double targety = 1200 + 578;
            if (ETeam.JAUNE == rsNerell.team()) {
                targetx = 3000 - targetx;
            }
            final Point target = new Point(targetx, targety);

            if (rsNerell.team() == ETeam.BLEU) {
                if (rsNerell.strategy() != ENerellStrategy.BASIC_NORD) {
                    mv.gotoPoint(220, 1290);
                    mv.gotoOrientationDeg(66);
                }

                mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                group.boueePrise(1, 2);

                mv.gotoOrientationDeg(0);
                mv.gotoPoint(770, targety, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                group.boueePrise(5);

                mv.gotoPoint(980, 1635, GotoOption.AVANT);
                group.boueePrise(6);

            } else {
                if (rsNerell.strategy() != ENerellStrategy.BASIC_NORD) {
                    mv.gotoPoint(3000 - 220, 1290);
                    mv.gotoOrientationDeg(180 - 66);
                }

                mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                group.boueePrise(13, 14);

                mv.gotoOrientationDeg(180);
                mv.gotoPoint(3000 - 770, targety, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                group.boueePrise(12);

                mv.gotoPoint(3000 - 980, 1635, GotoOption.AVANT);
                group.boueePrise(11);
            }

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            complete();
        }
    }
}