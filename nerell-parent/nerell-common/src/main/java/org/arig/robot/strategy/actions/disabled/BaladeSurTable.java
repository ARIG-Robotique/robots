package org.arig.robot.strategy.actions.disabled;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaladeSurTable extends AbstractEurobotAction {
    private int nbTry = 0;

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
        if (nbTry == 0) {
            return new Point(getX(1500), 1000);
        } else if (nbTry == 1) {
            return new Point(getX(1000), 1300);
        } else if (nbTry == 2) {
            return new Point(getX(1825), 815);
        } else if (nbTry == 3) {
            return new Point(getX(1180), 815);
        } else if (nbTry == 4) {
            return new Point(getX(1865), 1165);
        }
        return null;
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean isValid() {
        return !ilEstTempsDeRentrer();
    }

    @Override
    public void execute() {
        try {
            // L'entry point calcul le chemin le plus court et défini gotoSite et destSite
            final Point entry = entryPoint();
            nbTry++;
            if (nbTry > 4) {
                nbTry = 0;
            }
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
