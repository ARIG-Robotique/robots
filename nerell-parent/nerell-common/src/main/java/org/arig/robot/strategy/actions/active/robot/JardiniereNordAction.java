package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.enums.TypeCalage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JardiniereNordAction extends AbstractJardiniereFromStockPots {

    @Override
    public String name() {
        return "Jardinière nord";
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        if (rs.potsInZoneDepart() > 0) {
            return super.entryPoint();
        } else {
            return entryDirect();
        }
    }

    public Point entryDirect() {
        return new Point(getX(762), 2000 - 230);
    }

    @Override
    public boolean isValid() {
        StockPots stockPots = rs.stocksPots().get(rs.team() == Team.BLEU ? StockPots.ID.BLEU_NORD : StockPots.ID.JAUNE_NORD);

        if (rs.potsInZoneDepart() > 0) {
            return super.isValid()
                    && rs.jardiniereNord().isEmpty()
                    && (!rs.jardiniereMilieu().isEmpty()
                    || stockPots.isBloque()
                    || stockPots.isPresent()
            );
        } else {
            return super.isValid()
                    && rs.jardiniereNord().isEmpty();
        }
    }

    @Override
    public int order() {
        return rs.potsInZoneDepart() > 0 ? 15 : 10; // trois plantes (avec ou sans pot)
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryDirect();
            if (rs.potsInZoneDepart() > 0) {
                gotoAndTake();

                // va à la jardinière
                mv.setVitessePercent(100, 100);
                rs.enableAvoidance();
                mv.gotoPoint(entry);
            } else {
                mv.setVitessePercent(100, 100);
                mv.pathTo(new Point(getX(450), 1775));
                mv.pathTo(entry);
            }

            runAsync(() -> {
                bras.setBrasAvant(new PointBras(225, 155, -90, null));
            });

            // callage en face de la jardinière
            mv.gotoOrientationDeg(90);

            rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
            mv.setVitessePercent(60, 200);
            mv.avanceMM(2000 - entry.getY() - config.distanceCalageAvant() - 10);

            if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
                depose(rs.jardiniereNord(), true);
                return;
            }

            mv.setVitessePercent(0, 100);
            rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
            mv.avanceMMSansAngle(40);
            checkRecalageYmm(2000 - config.distanceCalageAvant(), TypeCalage.AVANT);

            if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
                depose(rs.jardiniereNord(), true);
                return;
            }

            depose(rs.jardiniereNord(), false);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
