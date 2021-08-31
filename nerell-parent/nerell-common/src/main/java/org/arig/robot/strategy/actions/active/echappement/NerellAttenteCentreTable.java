package org.arig.robot.strategy.actions.active.echappement;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellAttenteCentreTable extends AbstractNerellAction {

    @Override
    public Point entryPoint() {
        if (rs.phare()) {
            return new Point(getX(850), 1750); // face à l'écueil
        } else {
            return new Point(getX(450), 250); // face aux manches à air
        }
    }

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ATTENTE_CENTRE_PREFIX + "Nerell";
    }

    @Override
    public int order() {
        return -200;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsNerell.inPort() && rs.getRemainingTime() > IEurobotConfig.validRetourPortRemainingTimeNerell;
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
