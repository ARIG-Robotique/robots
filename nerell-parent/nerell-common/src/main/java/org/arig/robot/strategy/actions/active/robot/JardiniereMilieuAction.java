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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class JardiniereMilieuAction extends AbstractJardiniereAction {

    @Autowired(required = false)
    private PoussePlanteNord actionPoussePlante;

    @Override
    public String name() {
        return "Jardinière centre";
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(300), 1390);
    }

    @Override
    public boolean isValid() {
        StockPots stockPots = stockPots();

        // en strat basique ou si plus de temps, on va pousser les pots
        return super.isValid()
                && (
                !stockPots.isBloque() && !stockPots.isPresent()
                        || rs.strategy() == Strategy.SUD
                        || rs.getRemainingTime() < EurobotConfig.validTimePrisePots);
    }

    @Override
    public Rectangle blockingZone() {
        final StockPots stockPots = stockPots();

        if (stockPots.isBloque() || stockPots.isPresent()) {
            if (rs.team() == Team.BLEU) {
                return new Rectangle(0, 775, 450, 450);
            } else {
                return new Rectangle(2545, 775, 450, 450);
            }
        }

        return null;
    }

    private StockPots stockPots() {
        return rs.stocksPots().get(rs.team() == Team.BLEU ? StockPots.ID.BLEU_NORD : StockPots.ID.JAUNE_NORD);
    }

    protected Jardiniere jardiniere() {
        return rs.jardiniereMilieu();
    }

    private void executeInternal(boolean arriere) throws AvoidingException {
        prepareBras(arriere);

        CompletableFuture<boolean[]> refreshBras;
        if (arriere) {
            refreshBras = bras.refreshPincesArriere();
        } else {
            refreshBras = bras.refreshPincesAvant();
        }

        rs.disableAvoidance();

        mv.setVitessePercent(60, 100);
        if (arriere) {
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMM(getX((int) mv.currentXMm()) - config.distanceCalageArriere() - 10);
        } else {
            rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
            mv.avanceMM(getX((int) mv.currentXMm()) - config.distanceCalageAvant() - 10);
        }

        if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
            refreshBras.join();
            depose(arriere, true);
            return;
        }

        mv.setVitessePercent(0, 100);
        if (arriere) {
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            mv.reculeMMSansAngle(40);
            checkRecalageXmm(getX((int) config.distanceCalageArriere()), TypeCalage.ARRIERE);
            checkRecalageAngleDeg(rs.team() == Team.BLEU ? 0 : 180);
        } else {
            rs.enableCalageBordure(TypeCalage.AVANT);
            mv.avanceMMSansAngle(40);
            checkRecalageXmm(getX((int) config.distanceCalageAvant()), TypeCalage.AVANT);
            checkRecalageAngleDeg(rs.team() == Team.BLEU ? 180 : 0);
        }

        refreshBras.join();
        depose(arriere, false);
    }

    @Override
    public void execute() {
        try {
            final Point pointApproche = new Point(getX(200), 1650);
            final Point entry = entryPoint();
            final StockPots stockPots = stockPots();

            boolean skipApproche = false;
            if (actionPoussePlante != null && actionPoussePlante.isValid()) {
                actionPoussePlante.execute(new Point(getX(450), 2000 - 225));
                skipApproche = true;
            }

            if (stockPots.isBloque() || stockPots.isPresent()) {
                mv.setVitessePercent(100, 100);
                // point intermédaire dans la zone nord pour ensuite pousser les pots
                //if (!skipApproche) {
                    mv.pathTo(pointApproche, GotoOption.AVANT);
                //}
                mv.setVitessePercent(50, 100);
                mv.gotoPoint(getX(170), 1550, GotoOption.ARRIERE);
                mv.gotoPoint(getX(170), 1250, GotoOption.ARRIERE);
                stockPots.pris();
                mv.gotoPoint(pointApproche.getX(), entry.getY());
                mv.gotoPoint(entry);

            } else {
                mv.setVitessePercent(100, 100);
                mv.pathTo(entry);
            }

            if (!rs.bras().arriereLibre()) {
                mv.gotoOrientationDeg(rs.team() == Team.BLEU ? 0 : 180);
                executeInternal(true);
            } else {
                mv.gotoOrientationDeg(rs.team() == Team.BLEU ? 180 : 0);
                executeInternal(false);
            }

            if (!jardiniere().rang2() && (!rs.bras().avantLibre() || !rs.stockLibre())) {
                mv.gotoOrientationDeg(rs.team() == Team.BLEU ? 180 : 0);
                executeInternal(false);
            }

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            runAsync(() -> bras.brasAvantInit());
        }
    }
}
