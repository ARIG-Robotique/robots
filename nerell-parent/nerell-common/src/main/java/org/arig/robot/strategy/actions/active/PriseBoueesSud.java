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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseBoueesSud extends AbstractNerellAction {

    @Autowired
    private Bouee8 bouee8;

    @Autowired
    private Bouee9 bouee9;

    private boolean firstExecution = true;

    @Override
    public String name() {
        return "Prise bouées sud";
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
        if (rs.strategy() == ENerellStrategy.BASIC_SUD && firstExecution) {
            return 1000;
        }
        return 6 + (rs.ecueilEquipePris() ? 0 : 10) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return rs.pincesAvantEmpty() &&
                rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime &&
                (rs.team() == ETeam.JAUNE && rs.grandChenalVertEmpty() || rs.grandChenalRougeEmpty());
    }

    @Override
    public void execute() {
        firstExecution = false;
        try {
            rs.enablePincesAvant();

            final Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            if (rs.strategy() != ENerellStrategy.BASIC_SUD && tableUtils.distance(entry) > 100) {
                mv.pathTo(entry);
            } else {
                // Le path active l'évittement en auto, pas de path, pas d'evittement
                rs.enableAvoidance();
            }

            double targetx = 434;
            double targety = 1200 - 570;
            if (ETeam.JAUNE == rs.team()) {
                targetx = 3000 - targetx;
            }
            final Point target = new Point(targetx, targety);

            if (rs.team() == ETeam.BLEU) {
                if (rs.strategy() != ENerellStrategy.BASIC_SUD) {
                    mv.gotoPoint(220, 1110);
                    mv.gotoOrientationDeg(-66);
                }

                mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                rs.boueePrise(3);
                rs.boueePrise(4);

                rs.disablePincesAvant();

                // en cas d'erreur sur bouee 9 ou 8
                complete();

                if (bouee8.isValid()) {
                    bouee8.execute();
                }
                if (bouee9.isValid()) {
                    bouee9.execute();
                }

            } else {
                if (rs.strategy() != ENerellStrategy.BASIC_SUD) {
                    mv.gotoPoint(3000 - 220, 1110);
                    mv.gotoOrientationDeg(-180 + 66);
                }

                mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                rs.boueePrise(15);
                rs.boueePrise(16);

                rs.disablePincesAvant();

                // en cas d'erreur sur bouee 9 ou 8
                complete();

                if (bouee9.isValid()) {
                    bouee9.execute();
                }
                if (bouee8.isValid()) {
                    bouee8.execute();
                }
            }

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            complete();
        }
    }
}
