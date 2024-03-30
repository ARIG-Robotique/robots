package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaladeSurTable extends AbstractEurobotAction {

    @Override
    public String name() {
        return "Balade sur table CARAR";
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(1500), 1000);
    }

    @Override
    public int order() {
        return 100; // C'est 10 points et puis c'est tout
    }

    @Override
    public boolean isValid() {
        return timeBeforeRetourValid();
    }

    @Override
    public void execute() {
        try {
            // L'entry point calcul le chemin le plus court et défini gotoSite et destSite
            final Point entry = entryPoint();

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);
            mv.pathTo(new Point(getX(1000), 1300));
            mv.pathTo(new Point(getX(1825), 815));
            mv.pathTo(new Point(getX(1180), 815));
            mv.pathTo(new Point(getX(1865), 1165));

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
        updateValidTime();
    }
}
