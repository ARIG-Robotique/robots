package org.arig.robot.config.spring;

import org.arig.robot.model.RobotName;

public class NerellUtilsContext extends NerellRobotContext {

    @Override
    public RobotName robotName() {
        final RobotName rn = super.robotName();
        rn.name("Nerell Shell");

        return rn;
    }
}
