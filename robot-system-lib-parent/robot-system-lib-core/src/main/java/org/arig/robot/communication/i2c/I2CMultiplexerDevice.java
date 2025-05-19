package org.arig.robot.communication.i2c;

public interface I2CMultiplexerDevice {
  boolean selectChannel(byte channel);

  void disable();
}
