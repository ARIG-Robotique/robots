package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.strategy.actions.disabled.atomfactory.PrendreGoldenium;
import org.springframework.stereotype.Component;

@Component
public class Basic4PrendreGoldenium extends PrendreGoldenium {
    @Override
    public int order() {
        return IStrategyOrder.PRISE_GOLDENIUM;
    }
}
