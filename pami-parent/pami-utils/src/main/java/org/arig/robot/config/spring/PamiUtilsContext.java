package org.arig.robot.config.spring;

import org.arig.robot.model.RobotName;

public class PamiUtilsContext extends PamiRobotContext {

    @Override
    public RobotName robotName() {
        final RobotName rn = super.robotName();
        rn.name("Pami Shell");

        return rn;
    }
}
