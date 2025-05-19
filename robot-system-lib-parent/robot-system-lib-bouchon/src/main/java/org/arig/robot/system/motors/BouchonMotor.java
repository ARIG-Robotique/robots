package org.arig.robot.system.motors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BouchonMotor extends AbstractMotor {

  public BouchonMotor(int offset, int min, int max) {
    super(offset);
    minVal = min;
    maxVal = max;
    prev = offset;
  }

  @Override
  public void init() {
  }

  @Override
  public void speed(int val) {
    if (this.reverse()) {
      val = -val;
    }
    final int cmd = check(val + offsetValue);
    if (cmd == prev) {
      return;
    }
    prev = cmd;
  }

  @Override
  public void printVersion() {
  }
}
