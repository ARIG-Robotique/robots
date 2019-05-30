package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.strategy.actions.disabled.atomfactory.PrendreGrandDistributeurEquipeCentre;
import org.springframework.stereotype.Component;

@Component
public class Basic6GrandDistributeurCentre extends PrendreGrandDistributeurEquipeCentre {
    @Override
    public int order() {
        return IStrategyOrder.PRISE_GRAND_DIS_2;
    }
}
