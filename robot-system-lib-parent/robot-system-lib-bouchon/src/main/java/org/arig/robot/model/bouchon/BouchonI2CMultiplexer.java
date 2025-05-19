package org.arig.robot.model.bouchon;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CMultiplexerDevice;

@Slf4j
public class BouchonI2CMultiplexer implements I2CMultiplexerDevice {
  @Override
  public boolean selectChannel(final byte channel) {
    log.info("Selection du canal {}", channel);
    return true;
  }

  @Override
  public void disable() {
    log.info("Désactivation du multiplexeur");
  }
}
