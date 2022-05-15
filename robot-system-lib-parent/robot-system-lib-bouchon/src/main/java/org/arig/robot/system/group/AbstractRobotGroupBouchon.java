package org.arig.robot.system.group;

public abstract class AbstractRobotGroupBouchon implements RobotGroup {

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean tryConnect() {
        return false;
    }

    @Override
    public void setCurrentAction(String name) {
    }

    @Override
    public void setCurrentPosition(int x, int y) {
    }

    @Override
    public <E extends Enum<E>> void sendEventLog(E event, byte[] data) {
    }

    @Override
    public void listen(Handler handler) {
    }
}
