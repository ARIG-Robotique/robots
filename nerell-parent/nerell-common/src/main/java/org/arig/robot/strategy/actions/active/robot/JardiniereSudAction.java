package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Jardiniere;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.active.robot.AbstractJardiniereAction;
import org.springframework.stereotype.Component;

import java.awt.*;

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
        StockPots stockPots = stockPots();

        // en strat basique ou si plus de temps, on va pousser les pots
        return super.isValid()
                && rs.strategy() != Strategy.BASIC
                && (
                !stockPots.isBloque() && !stockPots.isPresent()
                        || rs.strategy() == Strategy.BASIC
                        || rs.getRemainingTime() < EurobotConfig.validTimePrisePots);
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
        prepareBras();

        rs.disableAvoidance();

        rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
        mv.setVitessePercent(60, 100);
        mv.avanceMM(getX((int) mv.currentXMm()) - config.distanceCalageAvant() - 10);

        if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
            depose(true);
            return;
        }

        mv.setVitessePercent(0, 100);
        rs.enableCalageBordure(TypeCalage.AVANT);
        mv.avanceMMSansAngle(40);
        checkRecalageXmm(getX((int) config.distanceCalageAvant()), TypeCalage.AVANT);
        checkRecalageAngleDeg(rs.team() == Team.BLEU ? 0 : 180);

        depose(false);
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();
            final StockPots stockPots = stockPots();

            if (stockPots.isBloque() || stockPots.isPresent()) {
                mv.setVitessePercent(100, 100);
                // point intermédaire dans la zone nord pour ensuite pousser les pots
                mv.pathTo(getX(185), 1740 - 775);
                mv.setVitessePercent(50, 80);
                mv.gotoPoint(getX(170), 1640 - 775, GotoOption.ARRIERE);
                mv.gotoPoint(getX(170), 1250 - 775, GotoOption.ARRIERE);
                stockPots.pris();
                mv.gotoOrientationDegSansDistance(35);
                mv.gotoPoint(entry);

            } else {
                mv.setVitessePercent(100, 100);
                mv.pathTo(entry);
            }

            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);

            executeInternal();

            if (!rs.stockLibre() && !jardiniere().rang2()) {
                executeInternal();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            runAsync(() -> bras.brasAvantInit());
        }
    }
}
