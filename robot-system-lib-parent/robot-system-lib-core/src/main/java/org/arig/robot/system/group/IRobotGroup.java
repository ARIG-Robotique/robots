package org.arig.robot.system.group;

public interface IRobotGroup {
    boolean isOpen();

    boolean tryConnect();

    String getCurrentAction();

    void sendEventLog();
}
