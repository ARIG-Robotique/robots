package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.strategy.actions.disabled.atomfactory.PrendreGrandDistributeurEquipeTableau;
import org.springframework.stereotype.Component;

@Component
public class Basic2GrandDistributeurTableau extends PrendreGrandDistributeurEquipeTableau {
    @Override
    public int order() {
        return IStrategyOrder.PRISE_GRAND_DIS_1;
    }
}
