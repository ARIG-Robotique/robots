package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinTest extends AbstractOdinAction {

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Odin Test";
    }

    @Override
    public Point entryPoint() {
        return null;
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean isValid() {
        return isTimeValid();
    }

    @Override
    public void execute() {
        try {
            rs.disableAvoidance();

            // TODO Test comme Nerell a faire
            mv.setVitesse(robotConfig.vitesse(3), robotConfig.vitesseOrientation(10));
            mv.gotoPoint(1200,1200, GotoOption.AVANT);
            mv.gotoPoint(100, 1200, GotoOption.AVANT);
            mv.gotoOrientationDeg(0);

            completed = true;
        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
