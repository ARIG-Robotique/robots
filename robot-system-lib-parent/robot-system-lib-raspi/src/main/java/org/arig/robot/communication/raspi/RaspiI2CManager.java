package org.arig.robot.communication.raspi;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.AbstractI2CManager;
import org.arig.robot.exception.I2CException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author gdepuille on 18/12/13.
 */
@Slf4j
public class RaspiI2CManager extends AbstractI2CManager<I2CDevice> {

    private final I2CBus busI2c;

    public RaspiI2CManager(I2CBus busI2c) {
        this.busI2c = busI2c;
    }

    /**
     * Enregistrement d'un device I2C
     *
     * @param deviceName Nom du device
     * @param address address du device.
     */
    public void registerDevice(final String deviceName, final int address, byte ... cmd) throws I2CException {
        try {
            super.registerDevice(deviceName, busI2c.getDevice(address), cmd);
        } catch (IOException e) {
            final String errorMessage = String.format("Impossible d'enregistrer le device %s a l'adresse 0x02X", deviceName, address);
            log.error(errorMessage, e);
            throw new I2CException(errorMessage, e);
        }
    }

    /**
     * Execute un scan afin de detecter si tous les devices enregistré sont bien présent.
     *
     * @throws I2CException
     */
    @Override
    protected void scan() throws I2CException {
        List<String> deviceNotFound = new ArrayList<>();

        // Contrôle que les devices enregistré sont bien présent.
        log.info("Verification des devices enregistrés");
        Set<String> deviceNames = getDeviceMap().keySet();
        for (String name : deviceNames) {
            I2CDevice device = getDevice(name);
            try {
                if (getDeviceQuery().containsKey(name)) {
                    sendData(name, getDeviceQuery().get(name));
                } else {
                    device.read();
                }
                log.info("Scan {} [OK]", name);
            } catch (IOException e) {
                log.warn("Impossible de communiquer avec le périphérique {} : {}", name, e.toString());
                deviceNotFound.add(name);
            }
        }

        if (!deviceNotFound.isEmpty()) {
            String errorMessage = "Tout les devices enregistrés ne sont pas disponible : " + StringUtils.join(deviceNotFound, ", ");
            log.error(errorMessage);
            throw new I2CException(errorMessage);
        }
    }

    @Override
    public void sendData(String deviceName, byte... datas) throws I2CException {
        try {
            getDevice(deviceName).write(datas, 0, datas.length);
        } catch (IOException e) {
            String message = String.format("Erreur lors de l'envoi sur le device %s", deviceName);
            log.error(message, e);
            throw new I2CException(message, e);
        }
    }

    @Override
    public byte getData(String deviceName) throws I2CException {
        try {
            int res = getDevice(deviceName).read();
            if (res >= 0) {
                return (byte) (res & 0xFF);
            }
            String message = String.format("Erreur de lecture de la carte %s. Code erreur %d", deviceName, res);
            log.error(message);
            throw new I2CException(message);

        } catch (IOException e) {
            String message = String.format("Erreur de lecture de la carte %s : %s", deviceName, e.toString());
            log.error(message);
            throw new I2CException(message, e);
        }
    }

    @Override
    public byte[] getData(String deviceName, int size) throws I2CException {
        try {
            byte [] result = new byte[size];
            int res = getDevice(deviceName).read(result, 0, size);
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
