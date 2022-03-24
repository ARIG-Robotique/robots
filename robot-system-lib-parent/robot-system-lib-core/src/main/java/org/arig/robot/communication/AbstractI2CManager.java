package org.arig.robot.communication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.exception.I2CException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The Class AbstractI2CManager.
 *
 * @author gdepuille
 */
@Slf4j
public abstract class AbstractI2CManager<D> implements I2CManager {

    private boolean status = false;

    @Getter(value = AccessLevel.PROTECTED)
    private final Map<String, I2CManagerDevice<D>> deviceMap = new TreeMap<>();

    @Getter(value = AccessLevel.PROTECTED)
    private final Map<String, I2CMultiplexerDevice> multiplexerDeviceMap = new TreeMap<>();

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
     * Execution du scan sur un device
     * @param device
     * @throws I2CException
     * @throws IOException
     */
    protected abstract void scanDevice(I2CManagerDevice<D> device) throws I2CException, IOException;

    /**
     * Execute scan.
     *
     * @throws I2CException the i2 c exception
     */
    public final void executeScan() throws I2CException {
        Assert.notEmpty(deviceMap, "Le mapping des cartes est obligatoire");

        final List<String> deviceNotFound = new ArrayList<>();
        final Consumer<I2CManagerDevice<D>> processScan = d -> {
            try {
                selectMuxIfNecessary(d);
                scanDevice(d);
                log.info("Scan du device {} [OK]", d.deviceName());
            } catch (IOException | I2CException e) {
                log.warn("Impossible de communiquer avec le périphérique {}", d.deviceName());
                deviceNotFound.add(d.deviceName());
            }
        };

        // Contrôle que les devices enregistré sont bien présent.
        log.info("Verification des devices enregistrés non multiplexé");
        getDeviceMap().values().stream()
                .filter(i2CDeviceI2CManagerDevice -> !i2CDeviceI2CManagerDevice.isMultiplexed())
                .collect(Collectors.toList())
                .forEach(processScan);

        if (deviceNotFound.isEmpty()) {
            log.info("Verification des devices enregistrés multiplexé");
            getDeviceMap().values().stream()
                    .filter(I2CManagerDevice::isMultiplexed)
                    .collect(Collectors.toList())
                    .forEach(processScan);

            log.info("Désactivation de tous les multiplexeurs");
            getMultiplexerDeviceMap().forEach((k, v) -> v.disable());
        }

        if (!deviceNotFound.isEmpty()) {
            status = false;
            String errorMessage = "Tout les devices enregistrés ne sont pas disponible : " + StringUtils.join(deviceNotFound, ", ");
            log.error(errorMessage);
            throw new I2CException(errorMessage);
        }

        status = true;
    }

    /**
     * Reset des cartes enregistrées
     *
     * @throws I2CException
     */
    @Override
    public void reset() throws I2CException {
        log.info("Reset des cartes enregistrées");
        deviceMap.clear();
        multiplexerDeviceMap.clear();
        status = false;
    }

    /**
     * Register device.
     *
     * @param device     the device descriptor
     */
    public final void registerDevice(I2CManagerDevice<D> device) {
        Assert.notNull(device, "Le device doit être précisé");
        Assert.hasText(device.deviceName(), "Le nom de la carte doit être précisé");

        if (deviceMap.containsKey(device.deviceName())) {
            throw new IllegalStateException(String.format("Le device I2C %s est déjà enregistré", device.deviceName()));
        }
        if (StringUtils.isNotBlank(device.multiplexerDeviceName())) {
            Assert.notNull(device.multiplexerChannel(), String.format("Le canal du multiplexeur doit être spécifié pour le device %s", device.deviceName()));
        }

        log.debug("Enregistrement de la carte {}.", device);
        deviceMap.put(device.deviceName(), device);
    }

    /**
     * Enregistrement de l'implementation d'un multiplexeur
     *
     * @param multiplexerDeviceName Nom du device I2C multiplexeur
     * @param multiplexerDevice     Implementation a enregistrer
     */
    @Override
    public final void registerMultiplexerDevice(String multiplexerDeviceName, I2CMultiplexerDevice multiplexerDevice) {
        Assert.hasText(multiplexerDeviceName, "Le nom du multiplexeur doit être spécifié");
        Assert.notNull(multiplexerDevice, "Implementation du multiplexeur doit être spécifié");

        log.debug("Enregistrement du multiplexeur {}", multiplexerDeviceName);
        multiplexerDeviceMap.put(multiplexerDeviceName, multiplexerDevice);
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
    protected I2CManagerDevice<D> getDevice(final String deviceName) throws I2CException {
        if (deviceMap.containsKey(deviceName)) {
            return deviceMap.get(deviceName);
        }

        // FallBack
        final String message = String.format("Carte inconnu : %s", deviceName);
        log.warn(message);
        throw new I2CException(message);
    }

    protected void selectMuxIfNecessary(I2CManagerDevice<D> device) {
        if (device.isMultiplexed()) {
            final I2CMultiplexerDevice mux = getMultiplexerDeviceMap().get(device.multiplexerDeviceName());
            mux.selectChannel(device.multiplexerChannel());
        }
    }
}
