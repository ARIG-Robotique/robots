package org.arig.robot.communication;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.AbstractI2CManager;
import org.arig.robot.communication.i2c.I2CManagerDevice;

/**
 * @author gdepuille on 15/03/15.
 */
@Slf4j
public class DummyI2CManager extends AbstractI2CManager<Byte> {

    @Override
    protected void scanDevice(final I2CManagerDevice<Byte> device) { }

    @Override
    public void sendData(String deviceName, byte... data) {}

    @Override
    public byte getData(String deviceName) {
        return 0;
    }

    @Override
    public byte[] getData(String deviceName, int size) {
        byte [] result = new byte[size];
        for (int i = 0 ; i < size ; i++) {
            result[i] = getData(deviceName);
        }
        return result;
    }
}
