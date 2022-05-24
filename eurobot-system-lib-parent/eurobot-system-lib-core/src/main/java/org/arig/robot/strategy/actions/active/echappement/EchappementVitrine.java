package org.arig.robot.strategy.actions.active.echappement;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EchappementVitrine extends AbstractEurobotAction {

    @Getter
    private final boolean completed = false;

    @Override
    public String name() {
        return EurobotConfig.ACTION_ECHAPPEMENT_VITRINE;
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(180), 1815);
    }

    @Override
    public int order() {
        return -100 + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.getRemainingTime() > EurobotConfig.validTimeEchappement
                && rs.statuetteDepose() && rs.echantillonCampementPris() && rs.galerieBleuComplete();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(config.vitesse(80), config.vitesseOrientation());
            mv.pathTo(entryPoint());

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'éxecution de l'action : {}", e.getMessage());
        }
    }
}
