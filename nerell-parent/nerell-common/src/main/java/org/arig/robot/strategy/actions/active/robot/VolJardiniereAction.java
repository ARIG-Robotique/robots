package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.JardiniereAdverse;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class VolJardiniereAction extends AbstractNerellAction {

    private static final int ENTRY_X = 315;

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public String name() {
        return "Vol jardinière";
    }

    @Override
    public boolean isValid() {
        return isTimeValid()
                && rs.vol()
                && !ilEstTempsDeRentrer()
                && rs.bras().avantLibre()
                && (rs.jardiniereAdverseMilieu().volable() || rs.jardiniereAdverseSud().volable());
    }

    @Override
    public int order() {
        return 3 // ce qu'on pose
                + 15 // ce que l'adversaire pert
                + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public Point entryPoint() {
        return entryPoint(jardiniere());
    }

    private Point entryPoint(JardiniereAdverse jardiniere) {
        if (jardiniere == rs.jardiniereAdverseSud()) {
            return pointSud();
        } else {
            return pointMilieu();
        }
    }

    private Point pointSud() {
        return new Point(getX(ENTRY_X), 610);
    }

    private Point pointMilieu() {
        return new Point(tableUtils.getX(rs.team() == Team.BLEU, ENTRY_X), 1390);
    }

    private JardiniereAdverse jardiniere() {
        if (rs.jardiniereAdverseMilieu().volable() && rs.jardiniereAdverseSud().volable()) {
            Point currentPositionMm = mv.currentPositionMm();
            double distSud = currentPositionMm.distance(pointSud());
            double distMilieu = currentPositionMm.distance(pointMilieu());

            if (Math.min(distSud, distMilieu) == distSud) {
                return rs.jardiniereAdverseSud();
            } else {
                return rs.jardiniereAdverseMilieu();
            }
        }

        if (rs.jardiniereAdverseMilieu().volable()) {
            return rs.jardiniereAdverseMilieu();
        }

        return rs.jardiniereAdverseSud();
    }

    private int getAngle(JardiniereAdverse jardiniere) {
        if (rs.team() == Team.BLEU) {
            return jardiniere == rs.jardiniereAdverseSud() ? 180 : 0;
        } else {
            return jardiniere == rs.jardiniereAdverseSud() ? 0 : 180;
        }
    }

    private StockPots stockPots(JardiniereAdverse jardiniere) {
        if (rs.team() == Team.BLEU) {
            return rs.stocksPots().get(jardiniere == rs.jardiniereAdverseSud() ? StockPots.ID.BLEU_MILIEU : StockPots.ID.JAUNE_NORD);
        } else {
            return rs.stocksPots().get(jardiniere == rs.jardiniereAdverseSud() ? StockPots.ID.JAUNE_MILIEU : StockPots.ID.BLEU_NORD);
        }
    }

    @Override
    public void execute() {
        final JardiniereAdverse jardiniere = jardiniere();

        try {
            final Point entry = entryPoint(jardiniere);
            final int angle = getAngle(jardiniere);

            log.info("Vol de la {}", jardiniere.name());

            mv.setVitessePercent(100, 100);
            mv.pathTo(entry, GotoOption.AVANT);
            mv.gotoOrientationDeg(angle);

            bras.setBrasAvant(new PointBras(215, 145, -90, null));
            servos.groupePinceAvantOuvert(false);

            // TODO si la vision dit pas de mines, on peut aller plus vite
            mv.setVitessePercent(40, 100);
            rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);

            int xFromBorder;
            if (rs.team() == Team.BLEU && jardiniere == rs.jardiniereAdverseSud()
                    || rs.team() == Team.JAUNE && jardiniere == rs.jardiniereAdverseMilieu()) {
                xFromBorder = (int) mv.currentXMm();
            } else {
                xFromBorder = tableUtils.getX(true, (int) mv.currentXMm());
            }
            mv.avanceMM(xFromBorder - config.distanceCalageAvant() - 10);

            if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
                jardiniere.blocked(true);
                stockPots(jardiniere).bloque();
                servos.groupePinceAvantFerme(false);
                mv.reculeMM(200);
                bras.brasAvantInit();
                return;
            }

            mv.setVitessePercent(0, 100);
            rs.enableCalageBordure(TypeCalage.AVANT);
            mv.avanceMMSansAngle(40);

            bras.setBrasAvant(PointBras.withY(80));
            servos.groupePinceAvantFerme(true);
            CompletableFuture<boolean[]> refreshBras = bras.refreshPincesAvant();
            ThreadUtils.sleep(200);
            bras.setBrasAvant(new PointBras(223, 155, -90, null));

            mv.setVitessePercent(100, 100);
            mv.reculeMM(150);
            mv.gotoOrientationDeg(-90);
            mv.avanceMM(100);
            bras.setBrasAvant(new PointBras(195, 80, -90, null));

            refreshBras.join();

            servos.groupePinceAvantOuvert(true);
            ThreadUtils.sleep(200);
            bras.setBrasAvant(PointBras.withY(130));
            mv.reculeMM(100);

            rs.aireDeDeposeSud().add(rs.bras().getAvant());
            rs.bras().setAvant(null, null, null);

            mv.gotoOrientationDeg(angle);
            bras.brasAvantInit();

            jardiniere.done(true);
            stockPots(jardiniere).pris();

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            jardiniere.validTime(System.currentTimeMillis());
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
