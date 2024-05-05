package org.arig.robot.system;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.system.group.AbstractRobotGroupOverSocket;

import java.util.concurrent.Executor;
import java.util.function.Function;

@Slf4j
public class RobotGroupOverSocket extends AbstractRobotGroupOverSocket {

    private final Function<AbstractRobotStatus, Boolean> groupOkFunction;
    private final AbstractRobotStatus rs;

    public RobotGroupOverSocket(AbstractRobotStatus rs, Function<AbstractRobotStatus, Boolean> groupOkFunction, int serverPort, String otherHost, int otherPort, Executor executor) {
        super(serverPort, otherHost, otherPort, executor);
        this.groupOkFunction = groupOkFunction;
        this.rs = rs;
    }

    @Override
    protected boolean groupOk() {
        return groupOkFunction.apply(rs);
    }
}
