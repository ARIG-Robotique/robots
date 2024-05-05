package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Jardiniere;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.active.robot.AbstractJardiniereAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JardiniereNordAction extends AbstractJardiniereAction {

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
        return new Point(getX(760), 1700);
    }

    protected Jardiniere jardiniere() {
        return rs.jardiniereNord();
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    private void executeInternal() throws AvoidingException {
        final Point entry = entryPoint();

        prepareBras();

        rs.disableAvoidance();

        rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
        mv.setVitessePercent(60, 100);
        mv.avanceMM(2000 - entry.getY() - config.distanceCalageAvant() - 10);

        if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
            depose(true);
        }

        mv.setVitessePercent(0, 100);
        rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
        mv.avanceMMSansAngle(40);
        checkRecalageYmm(2000 - config.distanceCalageAvant(), TypeCalage.AVANT);
        checkRecalageAngleDeg(90);

        depose(false);
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();

            mv.setVitessePercent(100, 100);
            // point intermédiare dans l'aire de dépose nord si on traine des trucs
            mv.pathTo(new Point(getX(450), 1775));
            mv.gotoPoint(entry);

            mv.gotoOrientationDeg(90);

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
