package org.arig.robot.strategy.actions.disabled.atomfactorybasic;

import org.arig.robot.constants.IStrategyOrder;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.strategy.actions.active.DeposeAccelerateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Basic3DeposeAccelerateur extends DeposeAccelerateur {
    @Autowired
    private RobotStatus rs;

    @Override
    public int order() {
        return IStrategyOrder.DEPOSE_ACCELERATEUR_1;
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
