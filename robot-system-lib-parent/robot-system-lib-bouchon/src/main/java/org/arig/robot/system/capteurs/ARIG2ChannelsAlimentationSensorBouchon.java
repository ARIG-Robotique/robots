package org.arig.robot.system.capteurs;

import org.arig.robot.model.capteurs.AlimentationSensorValue;
import org.arig.robot.system.capteurs.i2c.AbstractAlimentationSensor;

public class ARIG2ChannelsAlimentationSensorBouchon extends AbstractAlimentationSensor {

  public ARIG2ChannelsAlimentationSensorBouchon(final String deviceName) {
    super(deviceName, 2);
  }

  public void mock(int channel, AlimentationSensorValue mock) {
    alimentations[channel] = mock;
  }

  @Override
  protected void getData() {
    // NOP
  }
}
