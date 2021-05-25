package org.arig.robot.system.group;

public interface IRobotGroup {
    boolean isOpen();

    String getCurrentAction();

    void sendEventLog();
}
