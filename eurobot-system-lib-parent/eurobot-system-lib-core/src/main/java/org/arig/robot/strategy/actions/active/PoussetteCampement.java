package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PoussetteCampement extends AbstractEurobotAction {

    protected final int X = 560;
    protected final int Y = 876;

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_DEPOSE_CAMPEMENT);
    }

    @Override
    public String name() {
        return EurobotConfig.ACTION_POUSSETTE_CAMPEMENT;
    }

    @Override
    public boolean isValid() {
        return !rs.poussetteCampementFaite() && rs.tailleCampementVertTemp() > 0
                && (rs.campementComplet() || rs.getRemainingTime() < 20000);
    }

    @Override
    public int order() {
        int points = rs.campementPointsPousette();
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(X), Y);
    }

    @Override
    public void refreshCompleted() {
        if (rs.poussetteCampementFaite()) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? -32 : -148);
            mv.reculeMM(100);
            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
            mv.reculeMM(150);
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(entry);

            group.pousetteCampementFaite();

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }

}
