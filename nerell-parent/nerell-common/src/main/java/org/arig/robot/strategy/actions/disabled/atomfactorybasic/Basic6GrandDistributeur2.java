package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.strategy.actions.active.PrendreGrandDistributeurEquipe2;
import org.springframework.stereotype.Component;

@Component
public class Basic6GrandDistributeur2 extends PrendreGrandDistributeurEquipe2 {
    @Override
    public int order() {
        return IStrategyOrder.PRISE_GRAND_DIS_2;
    }
}
