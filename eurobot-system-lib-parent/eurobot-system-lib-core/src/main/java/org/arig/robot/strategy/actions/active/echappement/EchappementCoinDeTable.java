package org.arig.robot.strategy.actions.active.echappement;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EchappementCoinDeTable extends AbstractEurobotAction {

    private int step = 0;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return EurobotConfig.ACTION_ECHAPPEMENT_COIN_TABLE_PREFIX + robotName.id().name();
    }

    @Override
    public Point entryPoint() {
        return null;
    }

    @Override
    public int order() {
        return -100;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.getRemainingTime() > 25000;
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            if (step == 0) {
                mv.pathTo(950, 1600, GotoOption.AVANT);
            } else if (step == 1) {
                mv.pathTo(2050, 800, GotoOption.AVANT);
            } else if (step == 2) {
                mv.pathTo(950, 800, GotoOption.AVANT);
            } else if (step == 3) {
                mv.pathTo(2050, 1600, GotoOption.AVANT);
            }

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.getMessage());
        } finally {
            step++;
            if (step >= 4) {
                step = 0;
            }
        }
    }
}
