package org.arig.test.robot.communication;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.AbstractI2CManager;
import org.arig.robot.exception.I2CException;

/**
 * @author gdepuille on 15/03/15.
 */
@Slf4j
public class DummyI2CManager extends AbstractI2CManager<Byte> {
    @Override
    protected void scan() throws I2CException {
        log.info("Dummy scan !");
    }

    @Override
    public void sendData(String deviceName, byte... datas) {}

    @Override
    public byte getData(String deviceName) {
        return (byte) Math.random();
    }

    @Override
    public byte[] getDatas(String deviceName, int size) {
        byte [] result = new byte[size];
        for (int i = 0 ; i < size ; i++) {
            result[i] = getData(deviceName);
        }
        return result;
    }
}
