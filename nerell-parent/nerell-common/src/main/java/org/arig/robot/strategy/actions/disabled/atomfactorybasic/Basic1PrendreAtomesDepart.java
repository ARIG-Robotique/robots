package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.strategy.actions.disabled.atomfactory.PrendreAtomesDepart;
import org.springframework.stereotype.Component;

@Component
public class Basic1PrendreAtomesDepart extends PrendreAtomesDepart {
    @Override
    public int order() {
        return IStrategyOrder.PRISE_ATOMES_DEPART;
    }
}
