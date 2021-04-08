package org.arig.robot.system.capteurs;

import org.arig.robot.exception.I2CException;
import org.arig.robot.model.capteurs.AlimentationSensorValue;

public class AlimentationSensorBouchon extends AbstractAlimentationSensor {

    public AlimentationSensorBouchon(final String deviceName, final byte nbChannels) {
        super(deviceName, nbChannels);
    }

    public void mock(int channel, AlimentationSensorValue mock) {
        alimentations[channel] = mock;
    }

    @Override
    protected void getData() {
        // NOP
    }
}
