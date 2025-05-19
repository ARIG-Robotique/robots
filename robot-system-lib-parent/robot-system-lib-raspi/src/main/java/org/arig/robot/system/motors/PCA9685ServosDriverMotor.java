package org.arig.robot.system.motors;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.Pin;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * NON TESTE
 */
public class PCA9685ServosDriverMotor extends AbstractMotor {

  public static int OFFSET = 1500;
  public static int MIN = 1100;
  public static int MAX = 1900;

  private final Pin motorPin;

  @Autowired
  private PCA9685GpioProvider pca9685;

  public PCA9685ServosDriverMotor(final Pin motorPin) {
    super(OFFSET);

    this.motorPin = motorPin;

    minVal = MIN;
    maxVal = MAX;
    prev = OFFSET;
  }

  @Override
  public void init() {
    stop();
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

    pca9685.setPwm(motorPin, cmd);
  }

  @Override
  public void printVersion() {
    // NOP
  }
}
