package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Strategy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinDecouverteCarreDeFouilleAction extends AbstractDecouverteCarreDeFouilleAction {

    @Override
    public int order() {
        if (rs.twoRobots() && rs.strategy() == Strategy.BASIC && nbTry == 0) {
            return 1000;
        }
        return super.order();
    }
}
