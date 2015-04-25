package org.arig.robot.communication;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.arig.robot.exception.I2CException;
import org.springframework.util.Assert;

/**
 * The Class AbstractI2CManager.
 * 
 * @author mythril
 */
@Slf4j
public abstract class AbstractI2CManager<D> implements II2CManager {

    /** The board map. */
    @Getter(value = AccessLevel.PROTECTED)
    private final Map<String, D> deviceMap = new HashMap<>();

    /**
     * Nb board registered.
     * 
     * @return the int
     */
    public int nbDeviceRegistered() {
        return deviceMap.size();
    }

    /**
     * Execute scan.
     * 
     * @throws I2CException
     *             the i2 c exception
     */
    public final void executeScan() throws I2CException {
        Assert.notEmpty(deviceMap, "Le mapping des cartes est obligatoire");
        scan();
    }

    @Override
    public void reset() throws I2CException {
        log.info("Reset des cartes enregistrés");
        deviceMap.clear();
    }

    /**
     * Scan.
     *
     * @throws I2CException
     *             the i2 c exception
     */
    protected abstract void scan() throws I2CException;

    /**
     * Register board.
     * 
     * @param deviceName
     *            the board name
     * @param device
     *            the address
     */
    public void registerDevice(final String deviceName, D device) {
        Assert.notNull(device, "Le device doit être précisé");
        Assert.hasText(deviceName, "Le nom de la carte doit être précisé");

        log.debug("Enregistrement de la carte {} ({}).", deviceName, device.toString());
        deviceMap.put(deviceName, device);
    }

    /**
     * Gets the board.
     * 
     * @param deviceName
     *            the board name
     * @return the device
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
