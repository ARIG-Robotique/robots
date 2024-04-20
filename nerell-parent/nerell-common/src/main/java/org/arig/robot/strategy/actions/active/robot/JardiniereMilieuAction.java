package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.StocksPots;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.enums.TypeCalage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JardiniereMilieuAction extends AbstractJardiniereFromStockPots {

    @Override
    public String name() {
        return "Jardinière centre";
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public int order() {
        return 15; // trois plantes en pot
    }

    @Override
    public boolean isValid() {
        StockPots stockPots = rs.stocksPots().get(rs.team() == Team.BLEU ? StockPots.ID.BLEU_NORD : StockPots.ID.JAUNE_NORD);
        return super.isValid()
                && rs.jardiniereMilieu().isEmpty()
                && rs.potsInZoneDepart() > 0
                && !stockPots.isBloque() && !stockPots.isPresent();
    }

    @Override
    public void execute() {
        try {
            gotoAndTake();

            // on est déjà en face de la jardinière
            final Point entry = entryPoint();

            runAsync(() -> {
                bras.setBrasAvant(new PointBras(225, 155, -90, null));
            });

            // callage en face de la jardinière
            mv.gotoOrientationDeg(rs.team() == Team.BLEU ? 180 : 0);

            rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
            mv.avanceMM(getX((int) entry.getX()) - config.distanceCalageAvant() - 10);

            if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
                depose(rs.jardiniereMilieu(), true);
                return;
            }

            mv.setVitessePercent(0, 100);
            rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
            mv.avanceMMSansAngle(40);
            checkRecalageXmm(getX((int) config.distanceCalageAvant()), TypeCalage.AVANT);

            if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
                depose(rs.jardiniereMilieu(), true);
                return;
            }

            depose(rs.jardiniereMilieu(), false);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
