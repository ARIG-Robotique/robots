package org.arig.robot.system.group;

public class RobotGroupTest implements IRobotGroup {
    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean tryConnect() {
        return false;
    }

    @Override
    public String getCurrentAction() {
        return "EMPTY";
    }

    @Override
    public void sendEventLog() {
    }
}
