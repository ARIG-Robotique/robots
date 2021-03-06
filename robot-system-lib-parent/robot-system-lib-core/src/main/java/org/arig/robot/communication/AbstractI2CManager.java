package org.arig.robot.communication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.exception.I2CException;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class AbstractI2CManager.
 *
 * @author gdepuille
 */
@Slf4j
public abstract class AbstractI2CManager<D> implements II2CManager {

    private boolean status = false;

    @Getter(value = AccessLevel.PROTECTED)
    private final Map<String, D> deviceMap = new TreeMap<>();

    @Getter(value = AccessLevel.PROTECTED)
    private final Map<String, byte[]> deviceQuery = new HashMap<>();

    @Override
    public boolean status() {
        return status;
    }

    /**
     * Nb device registered.
     *
     * @return the int
     */
    public int nbDeviceRegistered() {
        return deviceMap.size();
    }

    /**
     * Execute scan.
     *
     * @throws I2CException the i2 c exception
     */
    public final void executeScan() throws I2CException {
        Assert.notEmpty(deviceMap, "Le mapping des cartes est obligatoire");
        scan();
        status = true;
    }

    @Override
    public void reset() throws I2CException {
        log.info("Reset des cartes enregistrés");
        deviceMap.clear();
        status = false;
    }

    /**
     * Scan.
     *
     * @throws I2CException the i2 c exception
     */
    protected abstract void scan() throws I2CException;

    /**
     * Register device.
     *
     * @param deviceName the device name
     * @param device     the address
     */
    public void registerDevice(final String deviceName, D device, byte ... cmd) {
        Assert.notNull(device, "Le device doit être précisé");
        Assert.hasText(deviceName, "Le nom de la carte doit être précisé");

        log.debug("Enregistrement de la carte {} ({}).", deviceName, device.toString());
        deviceMap.put(deviceName, device);

        if (ArrayUtils.isNotEmpty(cmd)) {
            deviceQuery.put(deviceName, cmd);
        }
    }

    /**
     * Gets the device.
     *
     * @param deviceName the device name
     *
     * @return the device
     *
     * @throws I2CException
     */
    public D getDevice(final String deviceName) throws I2CException {
        if (deviceMap.containsKey(deviceName)) {
            return deviceMap.get(deviceName);
        }

        // FallBack
        final String message = String.format("Carte inconnu : %s", deviceName);
        log.warn(message);
        throw new I2CException(message);
    }
}
