package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
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
            rs.enableAvoidance();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(2050,1600, GotoOption.AVANT);
            mv.pathTo(950, 800, GotoOption.AVANT);
            mv.pathTo(2050, 800, GotoOption.AVANT);
            mv.pathTo(950, 1600, GotoOption.AVANT);

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.getMessage());
        }
    }
}
