package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Jardiniere;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.TypeCalage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class JardiniereNordAction extends AbstractJardiniereAction {

    @Autowired(required = false)
    private PoussePlanteNord actionPoussePlante;

    @Override
    public String name() {
        return EurobotConfig.ACTION_JARDINIERE_NORD;
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(760), 1700);
    }

    protected Jardiniere jardiniere() {
        return rs.jardiniereNord();
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    private void executeInternal(boolean arriere) throws AvoidingException {
        final Point entry = entryPoint();

        CompletableFuture<?> refresh = prepareBras(arriere);

        rs.disableAvoidance();

        mv.setVitessePercent(60, 100);
        if (arriere) {
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMM(2000 - entry.getY() - config.distanceCalageArriere() - 10);
        } else {
            rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
            mv.avanceMM(2000 - entry.getY() - config.distanceCalageAvant() - 10);
        }

        if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
            refresh.join();
            depose(arriere, true);
        }

        mv.setVitessePercent(0, 100);
        if (arriere) {
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMMSansAngle(40);
            checkRecalageYmm(2000 - config.distanceCalageArriere(), TypeCalage.ARRIERE);
            checkRecalageAngleDeg(-90);
        } else {
            rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
            mv.avanceMMSansAngle(40);
            checkRecalageYmm(2000 - config.distanceCalageAvant(), TypeCalage.AVANT);
            checkRecalageAngleDeg(90);
        }

        refresh.join();
        depose(arriere, false);
    }

    @Override
    public void execute() {
        try {
            final Point pointApproche = new Point(getX(450), 1775);
            final Point entry = entryPoint();

            if (actionPoussePlante != null && actionPoussePlante.isValid() && !actionPoussePlante.isCompleted()) {
                actionPoussePlante.execute(pointApproche);
            } else {
                mv.setVitessePercent(100, 100);
                mv.pathTo(pointApproche);
            }

            mv.gotoPoint(entry);

            if (!rs.bras().arriereLibre()) {
                mv.gotoOrientationDeg(-90);
                executeInternal(true);
            } else {
                mv.gotoOrientationDeg(90);
                executeInternal(false);
            }

            if (!jardiniere().rang2() && (!rs.bras().avantLibre() || !rs.stockLibre())) {
                mv.gotoOrientationDeg(90);
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
