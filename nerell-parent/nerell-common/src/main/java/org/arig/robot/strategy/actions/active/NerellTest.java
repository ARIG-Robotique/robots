package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellTest extends AbstractNerellAction {

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Nerell Test";
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
            rsNerell.disableAvoidance();
            mv.gotoPoint(1500, 1000);
            completed = true;

        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
