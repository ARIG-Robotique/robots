package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.strategy.actions.active.PrendreGrandDistributeurEquipe1;
import org.springframework.stereotype.Component;

@Component
public class Basic2GrandDistributeur1 extends PrendreGrandDistributeurEquipe1 {
    @Override
    public int order() {
        return IStrategyOrder.PRISE_GRAND_DIS_1;
    }
}
