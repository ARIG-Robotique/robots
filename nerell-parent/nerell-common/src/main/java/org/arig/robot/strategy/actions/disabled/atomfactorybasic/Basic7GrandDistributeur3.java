package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.strategy.actions.active.PrendreGrandDistributeurEquipe3;
import org.springframework.stereotype.Component;

@Component
public class Basic7GrandDistributeur3 extends PrendreGrandDistributeurEquipe3 {
    @Override
    public int order() {
        return IStrategyOrder.PRISE_GRAND_DIS_3;
    }
}
