package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Jardiniere;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

/**
 * ATTENTION subtilité !!!!
 * getX est surchargé pour fonctionner dans l'autre sens
 * sinon c'est presque la même implém que jardiniyère milieu
 */
@Slf4j
@Component
public class JardiniereSudAction extends AbstractJardiniereAction {

    @Override
    protected int getX(int x) {
        return tableUtils.getX(rs.team() == Team.BLEU, x);
    }

    @Override
    public String name() {
        return "Jardinière sud";
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(300), 1390-775);
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    @Override
    public int order() {
        return super.order() + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public Rectangle blockingZone() {
        final StockPots stockPots = stockPots();

        if (stockPots.isBloque() || stockPots.isPresent()) {
            if (rs.team() == Team.BLEU) {
                return new Rectangle(2545, 0, 450, 450);
            } else {
                return new Rectangle(0, 0, 450, 450);
            }
        }

        return null;
    }

    private StockPots stockPots() {
        return rs.stocksPots().get(rs.team() == Team.BLEU ? StockPots.ID.JAUNE_MILIEU : StockPots.ID.BLEU_MILIEU);
    }

    protected Jardiniere jardiniere() {
        return rs.jardiniereSud();
    }

    private void executeInternal() throws AvoidingException {
        CompletableFuture<?> refreshBras = prepareBras(false);

        rs.disableAvoidance();

        mv.setVitessePercent(60, 100);
        rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
        mv.avanceMM(getX((int) mv.currentXMm()) - config.distanceCalageAvant() - 10);

        if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
            refreshBras.join();
            depose(false, true);
            return;
        }

        mv.setVitessePercent(0, 100);
        rs.enableCalageBordure(TypeCalage.AVANT);
        mv.avanceMMSansAngle(40);
        checkRecalageXmm(getX((int) config.distanceCalageAvant()), TypeCalage.AVANT);
        checkRecalageAngleDeg(rs.team() == Team.BLEU ? 0 : 180);

        refreshBras.join();
        depose(false, false);
    }

    @Override
    public void execute() {
        try {
            final Point pointApproche = new Point(getX(200), 1650 - 775);
            final Point entry = entryPoint();
            final StockPots stockPots = stockPots();

            // on pousse toujours les pots
            //if (stockPots.isBloque() || stockPots.isPresent()) {
                mv.setVitessePercent(100, 100);
                // point intermédaire dans la zone nord pour ensuite pousser les pots
                mv.pathTo(pointApproche, GotoOption.AVANT);
                mv.setVitessePercent(50, 80);
                mv.gotoPoint(getX(170), 1550 - 775, GotoOption.ARRIERE);
                mv.gotoPoint(getX(170), 1250 - 775, GotoOption.ARRIERE);
                stockPots.pris();
                mv.gotoPoint(pointApproche.getX(), entry.getY());
                mv.gotoPoint(entry);

//            } else {
//                mv.setVitessePercent(100, 100);
//                mv.pathTo(entry);
//            }

            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);
            executeInternal();

            if (!jardiniere().rang2() && !rs.stockLibre()) {
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);
                executeInternal();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();

            if (e instanceof MovementCancelledException) {
                complete();
            }

        } finally {
            runAsync(() -> bras.brasAvantInit());
        }
    }
}
