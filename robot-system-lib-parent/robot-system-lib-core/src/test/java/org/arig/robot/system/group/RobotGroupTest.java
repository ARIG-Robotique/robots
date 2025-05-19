package org.arig.robot.system.group;

public class RobotGroupTest implements RobotGroup {
  @Override
  public void listen(Handler handler) {
  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public boolean tryConnect() {
    return false;
  }

  @Override
  public void setCurrentAction(final String name) {
  }

  @Override
  public void setCurrentPosition(int x, int y) {
  }

  @Override
  public <E extends Enum<E>> void sendEventLog(E event, byte[] data) {
  }
}
