package org.arig.robot.system.capteurs.i2c;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.communication.i2c.I2CMultiplexerDevice;
import org.arig.robot.exception.I2CException;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class TCA9548MultiplexerI2C implements I2CMultiplexerDevice {

  private static byte DISABLED_MUX = -1;

  @Autowired
  private I2CManager i2cManager;

  private final String deviceName;

  private byte lastSelectedChannel = DISABLED_MUX;

  public TCA9548MultiplexerI2C(final String deviceName) {
    this.deviceName = deviceName;
  }

  @Override
  public boolean selectChannel(byte channel) {
    if (channel != lastSelectedChannel) {
      try {
        i2cManager.sendData(deviceName, (byte) (1 << channel));
        lastSelectedChannel = channel;
        ThreadUtils.sleep(1);
      } catch (I2CException e) {
        log.error("Impossible de selectionner le port {} du multiplexeur {}", channel, deviceName);
        return false;
      }
    }

    return true;
  }

  @Override
  public void disable() {
    try {
      i2cManager.sendData(deviceName, (byte) 0x00);
      lastSelectedChannel = DISABLED_MUX;
    } catch (I2CException e) {
      log.error("Impossible de désactiver le multiplexeur {}", deviceName);
    }
  }
}
