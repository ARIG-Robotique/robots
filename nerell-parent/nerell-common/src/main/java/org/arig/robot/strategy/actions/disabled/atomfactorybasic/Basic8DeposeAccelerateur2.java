package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.strategy.actions.disabled.atomfactory.DeposeAccelerateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Basic8DeposeAccelerateur2 extends DeposeAccelerateur {
    @Autowired
    private RobotStatus rs;

    @Override
    public int order() {
        return IStrategyOrder.DEPOSE_ACCELERATEUR_2;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                (
                        !rs.isAccelerateurOuvert() ||
                                canDepose()
                );
    }
}
