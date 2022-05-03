package org.arig.robot.strategy.actions.disabled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class EchappementCoinDeTable extends AbstractEurobotAction {

    private int step = 0;

    private final Random rand = new Random();

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
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            if (step == 0) {
                mv.pathTo(getX(520), 370);
            } else if (step == 1) {
                mv.pathTo(getX(1500), 1500);
            } else if (step == 2) {
                mv.pathTo(getX(2595), 370);
            } else if (step == 3) {
                mv.pathTo(getX(1500), 190);
            } else if (step == 4) {
                mv.pathTo(getX(240), 1800);
            } else if (step == 5) {
                mv.pathTo(getX(2420), 1720);
            }

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'éxecution de l'action : {}", e.getMessage());
        } finally {
            int newStep;
            do {
                newStep = rand.nextInt(6);
            } while (newStep == step);
            step = newStep;
        }
    }
}
