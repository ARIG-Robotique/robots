package org.arig.robot.communication.can;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.exception.CANException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * The Class AbstractCANManager.
 *
 * @author gdepuille
 */
@Slf4j
@Accessors(fluent = true)
public abstract class AbstractCANManager<D> implements CANManager {

    @Getter
    private boolean scanStatus = false;

    @Getter(value = AccessLevel.PROTECTED)
    private final Map<String, CANManagerDevice<D>> deviceMap = new TreeMap<>();

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
     * @throws CANException
     * @throws IOException
     */
    protected abstract void scanDevice(CANManagerDevice<D> device) throws CANException, IOException;

    /**
     * Execute scan.
     *
     * @throws CANException the i2 c exception
     */
    public final void executeScan() throws CANException {
        Assert.notEmpty(deviceMap, "Le mapping des cartes CAN est obligatoire");

        final List<String> deviceNotFound = new ArrayList<>();
        final Consumer<CANManagerDevice<D>> processScan = d -> {
            try {
                scanDevice(d);
                log.info("Scan du device {} [OK]", d.deviceName());
            } catch (IOException | CANException e) {
                log.warn("Scan du device {} [KO]", d.deviceName());
                deviceNotFound.add(d.deviceName());
            }
        };

        // Contrôle que les devices enregistré sont bien présent.
        log.info("Verification des devices enregistrés");
        deviceMap().values().forEach(processScan);

        if (!deviceNotFound.isEmpty()) {
            scanStatus = false;
            String errorMessage = "Tout les devices enregistrés ne sont pas disponible : " + StringUtils.join(deviceNotFound, ", ");
            log.error(errorMessage);
            throw new CANException(errorMessage);
        }

        scanStatus = true;
    }

    /**
     * Reset des cartes enregistrées
     *
     * @throws CANException
     */
    @Override
    public void reset() throws CANException {
        log.info("Reset des cartes enregistrées");
        deviceMap.clear();
        scanStatus = false;
    }

    /**
     * Register device.
     *
     * @param device     the device descriptor
     */
    public final void registerDevice(CANManagerDevice<D> device) {
        Assert.notNull(device, "Le device doit être précisé");
        Assert.hasText(device.deviceName(), "Le nom de la carte doit être précisé");

        if (deviceMap.containsKey(device.deviceName())) {
            throw new IllegalStateException(String.format("Le device CAN %s est déjà enregistré", device.deviceName()));
        }

        log.debug("Enregistrement de la carte {}.", device);
        deviceMap.put(device.deviceName(), device);
    }

    /**
     * Gets the device.
     *
     * @param deviceName the device name
     *
     * @return the device
     *
     * @throws CANException
     */
    protected CANManagerDevice<D> getDevice(final String deviceName) throws CANException {
        if (deviceMap.containsKey(deviceName)) {
            return deviceMap.get(deviceName);
        }

        // FallBack
        final String message = String.format("Carte inconnu : %s", deviceName);
        log.warn(message);
        throw new CANException(message);
    }
}
