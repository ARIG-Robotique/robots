package org.arig.robot.system.group;

public abstract class AbstractRobotGroupBouchon implements IRobotGroup {

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
        return null;
    }

    @Override
    public <E extends Enum<E>> void sendEventLog(E event, byte[] data) {
    }

    @Override
    public void listen(Handler handler) {
    }
}
