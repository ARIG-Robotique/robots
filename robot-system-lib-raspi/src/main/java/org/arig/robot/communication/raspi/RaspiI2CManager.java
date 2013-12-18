package org.arig.robot.communication.raspi;

import com.pi4j.io.i2c.I2CDevice;
import org.arig.robot.communication.AbstractI2CManager;
import org.arig.robot.exception.I2CException;

/**
 * Created by mythril on 18/12/13.
 */
public class RaspiI2CManager extends AbstractI2CManager<I2CDevice> {

    @Override
    protected void scan() throws I2CException {

    }

    @Override
    public void reset() throws I2CException {

    }

    @Override
    public boolean isError(Byte returnCode) {
        return false;
    }

    @Override
    public void printError(Byte returnCode) {

    }

    @Override
    public byte sendData(String deviceName, byte... datas) {
        return 0;
    }

    @Override
    public byte sendData(String deviceName, int nbResult, byte... datas) {
        return 0;
    }

    @Override
    public byte getData(String deviceName) {
        return 0;
    }

    @Override
    public byte[] getDatas(String deviceName) {
        return new byte[0];
    }
}
