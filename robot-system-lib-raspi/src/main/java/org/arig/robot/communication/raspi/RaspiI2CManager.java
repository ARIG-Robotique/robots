package org.arig.robot.communication.raspi;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.AbstractI2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by mythril on 18/12/13.
 */
@Slf4j
public class RaspiI2CManager extends AbstractI2CManager<I2CDevice> {

    @Autowired
    private I2CBus busI2c;

    /**
     * Enregistrement d'un de
     *
     * @param deviceName Nom du device
     * @param address address du device.
     */
    public void registerDevice(final String deviceName, final int address) throws I2CException {
        try {
            super.registerDevice(deviceName, busI2c.getDevice(address));
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
        RaspiI2CManager.log.info("Verification des devices enregistrés");
        Set<String> deviceNames = getDeviceMap().keySet();
        for (String name : deviceNames) {
            I2CDevice device = getDevice(name);
            try {
                device.read();
            } catch (IOException e) {
                RaspiI2CManager.log.warn(String.format("Impossible de communiqué avec le device %s (%s) : %s", name, device.toString(), e.toString()));
                deviceNotFound.add(name);
            }
        }

        if (!deviceNotFound.isEmpty()) {
            String errorMessage = "Tout les devices enregistré ne sont pas disponible : " + StringUtils.join(deviceNotFound, ", ");
            RaspiI2CManager.log.error(errorMessage);
            throw new I2CException(errorMessage);
        }
    }

    @Override
    public void sendData(String deviceName, byte... datas) throws I2CException {
        try {
            getDevice(deviceName).write(datas, 0, datas.length);
        } catch (IOException e) {
            log.error(String.format("Erreur lors de l'envoi sur le device %s", deviceName));
            throw new I2CException("Erreur d'ecriture de la carte " + deviceName, e);
        }
    }

    @Override
    public void sendData(String deviceName, int nbResult, byte... datas) throws I2CException {
        sendData(deviceName, datas);
    }

    @Override
    public byte getData(String deviceName) throws I2CException {
        try {
            return (byte) getDevice(deviceName).read();
        } catch (IOException e) {
            log.error("Erreur de lecture de la carte " + deviceName + " : " + e.toString());
            throw new I2CException("Erreur de lecture de la carte " + deviceName, e);
        }
    }

    @Override
    public byte[] getDatas(String deviceName, int size) throws I2CException {
        try {
            byte [] result = new byte[size];
            getDevice(deviceName).read(result, 0, size);
            return result;
        } catch (IOException e) {
            log.error("Erreur de lecture de la carte " + deviceName + " : " + e.toString());
            throw new I2CException("Erreur de lecture de la carte " + deviceName, e);
        }
    }
}
