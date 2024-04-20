package org.arig.robot.strategy.actions.disabled;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Plante;
import org.arig.robot.model.Point;
import org.arig.robot.model.TypePlante;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PoussePlante2 extends AbstractEurobotAction {

    @Override
    public String name() {
        return "Pousse plantes CARAR 2";
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(1775), 575);
    }

    @Override
    public int order() {
        return 500; // C'est 10 points et puis c'est tout
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

            mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
            mv.pathTo(entry);

            mv.setVitesse(300, config.vitesseOrientation());
            mv.gotoPoint(new Point(getX(2710), 1000), GotoOption.AVANT);

            rs.aireDeDeposeMilieu().addRang1(0, new Plante(TypePlante.FRAGILE));
            rs.aireDeDeposeMilieu().addRang1(1, new Plante(TypePlante.FRAGILE));
            rs.aireDeDeposeMilieu().addRang1(2, new Plante(TypePlante.FRAGILE));
            rs.aireDeDeposeMilieu().addRang2(0, new Plante(TypePlante.FRAGILE));
            rs.aireDeDeposeMilieu().addRang2(1, new Plante(TypePlante.RESISTANTE));
            rs.aireDeDeposeMilieu().addRang2(2, new Plante(TypePlante.RESISTANTE));

            mv.reculeMM(200);

            complete(true);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
