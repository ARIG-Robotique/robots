package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.I2CException;

@Slf4j
public class ARIG2ChannelsAlimentationSensor extends AbstractAlimentationSensor {

    public ARIG2ChannelsAlimentationSensor(final String deviceName) {
        super(deviceName, 2);
    }

    @Override
    protected void getData() throws I2CException {
        final byte[] data = i2cManager.getData(deviceName, (alimentations.length * 4) + 1);

        int faultByte = alimentations.length * 4;
        for (int channel = 0 ; channel < alimentations.length ; channel++) {
            int firstByte = channel * 4;

            // 0-1           : Alim tension
            // 2-3           : Alim current
            // last byte + 1 : Alim fault
            double rawTension = ((double) (data[firstByte] << 8)) + (data[firstByte + 1] & 0xff);
            double rawCurrent = ((double) (data[firstByte + 2] << 8)) + (data[firstByte + 3] & 0xff);
            boolean fault = (data[faultByte] & (channel + 1)) == 1;
            alimentations[channel].tension(rawTension / 100);
            alimentations[channel].current(rawCurrent / 100);
            alimentations[channel].fault(fault);
        }
    }
}
