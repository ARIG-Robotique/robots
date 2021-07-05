package org.arig.robot.strategy.actions.active.echappement;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinAttenteCentreTable extends AbstractOdinAction {

    @Override
    public Point entryPoint() {
        return new Point(getX(1500), 1200);
    }

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ATTENTE_CENTRE_PREFIX + "Odin";
    }

    @Override
    public int order() {
        return -200;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsOdin.inPort();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance(); // Pour eviter de se rentrer dans l'autre robot
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entryPoint());
        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur exécution de l'action : {}", e.toString());
        }
    }
}
