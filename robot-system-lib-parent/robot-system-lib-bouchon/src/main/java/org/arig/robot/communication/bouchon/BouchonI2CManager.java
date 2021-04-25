package org.arig.robot.communication.bouchon;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.AbstractI2CManager;
import org.arig.robot.communication.I2CManagerDevice;
import org.arig.robot.model.bouchon.BouchonI2CDevice;

/**
 * @author gdepuille on 18/12/13.
 */
@Slf4j
public class BouchonI2CManager extends AbstractI2CManager<BouchonI2CDevice> {

    @Override
    protected void scanDevice(final I2CManagerDevice<BouchonI2CDevice> device) { }

    @Override
    public void sendData(String deviceName, byte... data) { }

    @Override
    public byte getData(String deviceName) {
        return 0;
    }

    @Override
    public byte[] getData(String deviceName, int size) {
        byte[] result = new byte[size];
        for (int i = 0 ; i < size ; i++) {
            result[i] = 0;
        }
        return result;
    }
}
