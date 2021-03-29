package org.arig.robot.system.capteurs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

@Slf4j
@Data
@AllArgsConstructor
public class ArigAlimentationSensor {

    // Command pour la récupération de la version de la carte
    private static final byte COMMAND_GET_VERSION = 'v';

    // Commande pour la récupération des convertion Analogique / Numérique
    private static final byte COMMAND_GET_DATA = 'g';

    @Autowired
    private II2CManager i2cManager;

    private final String deviceName;

    private ArigAlimentationValues[] alimentations = {new ArigAlimentationValues(), new ArigAlimentationValues()};

    public ArigAlimentationSensor(String deviceName) {
        this.deviceName = deviceName;
    }

    public void printVersion() throws I2CException {
        try {
            i2cManager.sendData(deviceName, COMMAND_GET_VERSION);
            final byte[] data = i2cManager.getDatas(deviceName, 10);
            final String version = new String(data, StandardCharsets.UTF_8);
            log.info("Carte {} version {}", deviceName, version);
        } catch (I2CException e) {
            String message = "Erreur lors de la récupération de la version de la carte " + deviceName;
            log.error(message);
            throw new I2CException(message, e);
        }
    }

    public ArigAlimentationValues getAlimentation(Channel channel) {
        return alimentations[channel.getSlot()];
    }

    public void refresh() throws I2CException {
        if (log.isDebugEnabled()) {
            log.debug("Lecture des valeurs d'alimentation");
        }
        try {
            i2cManager.sendData(deviceName, COMMAND_GET_DATA);
        } catch (I2CException e) {
            String message = "Impossible de faire la lecture des valeurs de la carte " + deviceName;
            log.error(message);
            throw new I2CException(message, e);
        }

        final byte[] data = i2cManager.getDatas(deviceName, 18);

        // 0-1     : Alim 1 tension
        // 2-3     : Alim 1 current
        // 8 bit 1 : Alim 1 fault
        double rawTension = ((double) (data[0] << 8)) + (data[1] & 0xff);
        double rawCurrent = ((double) (data[2] << 8)) + (data[3] & 0xff);
        boolean fault = (data[8] & 0x01) == 1;
        alimentations[Channel.ALIMENTATION_1.getSlot()].tension = rawTension / 100;
        alimentations[Channel.ALIMENTATION_1.getSlot()].current = rawCurrent / 100;
        alimentations[Channel.ALIMENTATION_1.getSlot()].fault = fault;

        // 4-5     : Alim 2 tension
        // 6-7     : Alim 2 current
        // 8 bit 2 : Alim 2 fault
        rawTension = ((double) (data[4] << 8)) + (data[5] & 0xff);
        rawCurrent = ((double) (data[6] << 8)) + (data[7] & 0xff);
        fault = (data[8] & 0x02) == 2;
        alimentations[Channel.ALIMENTATION_2.getSlot()].tension = rawTension / 100;
        alimentations[Channel.ALIMENTATION_2.getSlot()].current = rawCurrent / 100;
        alimentations[Channel.ALIMENTATION_2.getSlot()].fault = fault;
    }

    @AllArgsConstructor
    public enum Channel {
        ALIMENTATION_1((byte) 0),
        ALIMENTATION_2((byte) 1);

        @Getter
        private final byte slot;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArigAlimentationValues {
        @Getter
        private double tension;
        @Getter
        private double current;
        @Getter
        private boolean fault;
    }
}
