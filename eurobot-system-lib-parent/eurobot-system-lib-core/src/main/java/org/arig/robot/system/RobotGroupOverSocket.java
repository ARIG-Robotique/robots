package org.arig.robot.system;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.group.AbstractRobotGroupOverSocket;

import java.util.concurrent.Executor;

@Slf4j
public class RobotGroupOverSocket extends AbstractRobotGroupOverSocket {

    public RobotGroupOverSocket(int serverPort, String otherHost, int otherPort, Executor executor) {
        super(serverPort, otherHost, otherPort, executor);
    }

}
