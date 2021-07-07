package org.arig.robot.strategy.actions.active.echappement;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AntiblocagePort extends AbstractEurobotAction {

    @Override
    public Point entryPoint() {
        return new Point(getX(460), 1200);
    }

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ANTIBLOCAGE_PORT;
    }

    @Override
    public int order() {
        return -100;
    }

    @Override
    public boolean isValid() {
        double x = conv.pulseToMm(position.getPt().getX());
        double y = conv.pulseToMm(position.getPt().getY());

        return isTimeValid() && y >= 800 && y <= 1600 && (x <= 400 || x >= 2600);
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance(); // Pour eviter de se rentrer dans l'autre robot
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(entryPoint());
        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur exécution de l'action : {}", e.toString());
        }
    }
}
