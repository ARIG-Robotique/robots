package org.arig.robot.communication.raspi;

import com.pi4j.io.i2c.I2CDevice;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.AbstractI2CManager;
import org.arig.robot.communication.i2c.I2CManagerDevice;
import org.arig.robot.exception.I2CException;

import java.io.IOException;

/**
 * @author gdepuille on 18/12/13.
 */
@Slf4j
public class RaspiI2CManager extends AbstractI2CManager<I2CDevice> {

    @Override
    protected void scanDevice(final I2CManagerDevice<I2CDevice> device) throws I2CException, IOException {
        if (device.scanCmd() != null) {
            sendData(device.deviceName(), device.scanCmd());
        } else {
            device.device().read();
        }
    }

    @Override
    public void sendData(String deviceName, byte... data) throws I2CException {
        try {
            final I2CManagerDevice<I2CDevice> registeredDevice = getDevice(deviceName);
            selectMuxIfNecessary(registeredDevice);
            registeredDevice.device().write(data, 0, data.length);
        } catch (IOException e) {
            String message = String.format("Erreur lors de l'envoi sur le device %s", deviceName);
            log.error(message, e);
            throw new I2CException(message, e);
        }
    }

    @Override
    public byte getData(String deviceName) throws I2CException {
        try {
            final I2CManagerDevice<I2CDevice> registeredDevice = getDevice(deviceName);
            selectMuxIfNecessary(registeredDevice);

            int res = registeredDevice.device().read();
            if (res >= 0) {
                return (byte) (res & 0xFF);
            }
            String message = String.format("Erreur de lecture de la carte %s. Code erreur %d", deviceName, res);
            log.error(message);
            throw new I2CException(message);

        } catch (IOException e) {
            String message = String.format("Erreur de lecture de la carte %s : %s", deviceName, e);
            log.error(message);
            throw new I2CException(message, e);
        }
    }

    @Override
    public byte[] getData(String deviceName, int size) throws I2CException {
        try {
            final I2CManagerDevice<I2CDevice> registeredDevice = getDevice(deviceName);
            selectMuxIfNecessary(registeredDevice);

            byte[] result = new byte[size];
            int res = registeredDevice.device().read(result, 0, size);
            if (log.isDebugEnabled()) {
                log.debug("Nombre de byte lu : {}", res);
            }
            return result;
        } catch (IOException e) {
            log.error("Erreur de lecture de la carte {} : {}", deviceName, e.toString());
            throw new I2CException("Erreur de lecture de la carte " + deviceName, e);
        }
    }
}
