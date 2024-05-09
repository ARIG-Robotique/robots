package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Plante;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPlantes;
import org.arig.robot.model.Team;
import org.arig.robot.model.TypePlante;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PoussePlanteNord extends AbstractNerellAction {

    @Override
    public String name() {
        return "Pousse plantes Nord";
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        return new Point(1500, 1000);
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public boolean isValid() {
        Plante.ID stockSurLePassage = rs.team() == Team.BLEU ? Plante.ID.STOCK_NORD_OUEST : Plante.ID.STOCK_NORD_EST;
        return new Point(1500, 1000).distance(mv.currentPositionMm()) < 700
                && !rs.plantes().stock(stockSurLePassage).isEmpty();
    }

    @Override
    public void execute() {
        execute(entryPoint());
    }

    public void execute(Point entry) {
        try {
            Plante.ID stockSurLePassage = rs.team() == Team.BLEU ? Plante.ID.STOCK_NORD_OUEST : Plante.ID.STOCK_NORD_EST;

            log.info("Poussage des plantes {} dans site {}", stockSurLePassage, rs.aireDeDeposeNord().siteDeCharge(rs.team()));

            rs.enableAvoidance();

            mv.setVitessePercent(100, 100);
            mv.pathTo(getX(1250), 1150, GotoOption.ARRIERE);

            mv.setVitessePercent(40, 100);
            mv.gotoPoint(entry);
            mv.avanceMM(100);

            runAsync(() -> bras.brasAvantInit());

            rs.plantes().priseStock(stockSurLePassage, StockPlantes.Status.EMPTY);
            rs.aireDeDeposeNord().add(new Plante[]{
                    new Plante(TypePlante.FRAGILE),
                    new Plante(TypePlante.FRAGILE),
                    new Plante(TypePlante.FRAGILE),
                    new Plante(TypePlante.FRAGILE),
                    new Plante(TypePlante.RESISTANTE),
                    new Plante(TypePlante.RESISTANTE)
            });
            rs.aireDeDeposeNord().rang1(true);
            rs.aireDeDeposeNord().rang2(true);

            complete(true);

        } catch (AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
