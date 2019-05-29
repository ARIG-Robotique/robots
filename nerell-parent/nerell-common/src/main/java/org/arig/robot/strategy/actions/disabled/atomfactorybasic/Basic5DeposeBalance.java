package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.strategy.actions.active.DeposerBalance;
import org.springframework.stereotype.Component;

@Component
public class Basic5DeposeBalance extends DeposerBalance {
    @Override
    public int order() {
        return IStrategyOrder.DEPOSE_BALANCE_1;
    }
}
