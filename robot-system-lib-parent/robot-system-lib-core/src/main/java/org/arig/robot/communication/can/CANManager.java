package org.arig.robot.communication.can;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.exception.CANException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The Class AbstractCANManager.
 *
 * @author gdepuille
 */
@Slf4j
@Accessors(fluent = true)
@RequiredArgsConstructor
public class CANManager {

    @Getter
    private boolean scanStatus = false;

    private final ArrayList<CANDevice> deviceMap;

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
     * @throws CANException the i2 c exception
     */
    public final void executeScan() throws CANException {
        Assert.notEmpty(deviceMap, "Le mapping des cartes CAN est obligatoire");

        final List<String> deviceNotFound = new ArrayList<>();
        final Consumer<CANDevice> processScan = d -> {
            try {
                d.scan();
                log.info("Scan du device {} [OK]", d.signature());
            } catch (IOException e) {
                log.warn("Scan du device {} [KO]", d.deviceName());
                deviceNotFound.add(d.deviceName());
            }
        };

        // Contrôle que les devices enregistré sont bien présent.
        log.info("Verification des devices enregistrés");
        deviceMap.forEach(processScan);

        if (!deviceNotFound.isEmpty()) {
            scanStatus = false;
            String errorMessage = "Tout les devices enregistrés ne sont pas disponible : " + StringUtils.join(deviceNotFound, ", ");
            log.error(errorMessage);
            throw new CANException(errorMessage);
        }

        scanStatus = true;
    }
}
