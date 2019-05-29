package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.strategy.actions.active.PrendreGrandDistributeurEquipeBalance;
import org.springframework.stereotype.Component;

@Component
public class Basic7GrandDistributeurBalance extends PrendreGrandDistributeurEquipeBalance {
    @Override
    public int order() {
        return IStrategyOrder.PRISE_GRAND_DIS_3;
    }
}
