package org.arig.robot.strategy.actions.active.echappement;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class EchappementCentreTable extends AbstractEurobotAction {

    private int step = 0;

    private final Random rand = new Random();

    @Getter
    private final boolean completed = false;

    @Override
    public String name() {
        return EurobotConfig.ACTION_ECHAPPEMENT_CENTRE_TABLE_PREFIX + robotName.id().name();
    }

    @Override
    public Point entryPoint() {
        if (step == 0) {
            return new Point(getX(1500), 1000);
        } else if (step == 1) {
            return new Point(getX(1500), 500);
        } else {
            return new Point(getX(1500), 1430);
        }
    }

    @Override
    public int order() {
        return -100;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && remainingTimeBeforeRetourSiteValid();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entryPoint());

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'éxecution de l'action : {}", e.getMessage());
        } finally {
            int newStep;
            do {
                newStep = rand.nextInt(3);
            } while (newStep == step);
            step = newStep;
        }
    }
}
