package org.arig.robot.config.spring;

import org.arig.robot.model.RobotName;

public class OdinUtilsContext extends OdinRobotContext {

    @Override
    public RobotName robotName() {
        final RobotName rn = super.robotName();
        rn.name("Odin Shell");

        return rn;
    }
}
