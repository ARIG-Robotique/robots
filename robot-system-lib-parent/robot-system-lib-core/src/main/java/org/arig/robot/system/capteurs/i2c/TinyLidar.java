package org.arig.robot.system.capteurs.i2c;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.filters.average.Average;
import org.arig.robot.filters.average.PassThroughValueAverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

/**
 * Classe d'interface avec le composant I2C tinyLidar de Micro Electronic Design
 *
 * /!\ Cette implémentation ne fonctionne pas en mode Real Time
 *
 * @author gdepuille
 * @see <a href="https://microed.co/tinylidar/">tinyLidar documentation</a>
 */
@Slf4j
public class TinyLidar  {

    private static final int INVALID_VALUE = -1;

    private static final byte READ_DATA_REGISTER = 0x44;
    private static final byte QUERY_SETTINGS_REGISTER = 0x51;
    private static final byte[] SET_CONTINUOUS_MODE = new byte[]{0x4d, 0x43};

    private static String UNKNOWN_RESULT = "** UNKNOWN **";

    @Autowired
    private I2CManager i2cManager;

    private final String deviceName;

    @Setter
    private Average<Integer> avg = new PassThroughValueAverage<>();

    public TinyLidar(String deviceName) {
        this.deviceName = deviceName;
/*
        try {
            i2cManager.sendData(deviceName, SET_CONTINUOUS_MODE);
        } catch (I2CException e) {
            log.warn("Impossible de passer le tinyLIDAR en continous mode");
        }

 */
    }

    @Async
    public Future<Integer> readValueAsync() {
        return new AsyncResult<>(readValue());
    }

    public Integer readValue() {
        if (log.isDebugEnabled()) {
            log.debug("Lecture du tinyLidar {}", deviceName);
        }
        try {
            i2cManager.sendData(deviceName, READ_DATA_REGISTER);

            // On lit 2 octets
            final byte[] data = i2cManager.getData(deviceName, 2);
            int res = ((short) ((data[0] << 8) + (data[1] & 0xFF)));

            // Si la distance est supérieur à 9 c'est OK
            if (res > 9) {
                if (log.isDebugEnabled()) {
                    log.debug("Résultat de la lecture du tinyLidar {} = {} mm", deviceName, res);
                }

                return avg.filter(res);

            } else {
                final String errorMessage;
                switch (res) {
                    case 1: errorMessage = "VL53L0 Status Code: Sigma Fail"; break;
                    case 2: errorMessage = "VL53L0 Status Code: Signal Fail"; break;
                    case 3: errorMessage = "VL53L0 Status Code: Min Range Fail"; break;
                    case 4: errorMessage = "VL53L0 Status Code: Phase Fail [Out of Range]"; break;
                    default: errorMessage = UNKNOWN_RESULT;
                }
                throw new ReadTinyLidarException(errorMessage);
            }
        } catch (I2CException | ReadTinyLidarException e) {
            log.error("Erreur de lecture du tinyLidar {} : {}", deviceName, e.getMessage());
            return INVALID_VALUE;
        }
    }

    public void printInformations() {
        try {
            i2cManager.sendData(deviceName, QUERY_SETTINGS_REGISTER);
            final byte[] data = i2cManager.getData(deviceName, 23); // Cf docs

            final String operationMode;
            switch (data[0]) {
                case 0x43: operationMode = "Continuous"; break;
                case 0x4c: operationMode = "Single Step / Ultra Low Power"; break;
                case 0x52: operationMode = "Real time"; break;
                default: operationMode = UNKNOWN_RESULT;
            }

            final String watchDog = (data[14] & 0x01) == 0 ? "OFF" : "ON";

            final String led;
            switch ((data[14] & 0x06) >> 1) {
                case 0: led = "OFF"; break;
                case 1: led = "ON"; break;
                case 2: led = "Measurement"; break;
                default: led = UNKNOWN_RESULT;
            }

            final String presetConfig;
            switch (data[1]) {
                case 0x53: presetConfig = "High Speed"; break;
                case 0x52: presetConfig = "Long Range"; break;
                case 0x41: presetConfig = "High Accuracy"; break;
                case 0x43: presetConfig = "Custom"; break;
                case 0x54: presetConfig = "tinyLiDAR"; break;
                default: presetConfig = UNKNOWN_RESULT;
            }

            final double signalRate = ((data[2] << 8) | (data[3] & 0xFF)) / 65536.0 + 0.005;
            final int timingBudget = (data[5] << 8) | (data[6] & 0xFF);

            final int preRange = data[7] == 14 ? 14 : 18;
            final int finalRange = data[7] == 14 ? 10 : 14;

            final String offsetCal = ((data[14] & 0x08) == 8) ? "Custom" : "Default";

            final int calOffset = (int) ((data[15] << 24 | data[16] << 16 | data[17] << 8 | data[18]) / 1000.0);
            final double xtalk = (data[19] << 24 | data[20] << 16 | data[21] << 8 | data[22]) / 65536.0;

            log.info("tinyLidar {} configuration :", deviceName);
            log.info(" * Current operation mode       : {}", operationMode);
            log.info(" * Watchdog                     : {}", watchDog);
            log.info(" * LED Indicator                : {}", led);
            log.info(" * Current Preset Configuration : {}", presetConfig);
            log.info(" * Signal Rate limit            : {} MCPS", signalRate);
            log.info(" * Sigma Estimate Limit         : {} mm", data[4]);
            log.info(" * Timing budget                : {} ms", timingBudget);
            log.info(" * Pre Range VCSEL Period       : {}", preRange);
            log.info(" * Final Range VCSEL Period     : {}", finalRange);
            log.info(" * Firmware Version             : {}.{}.{}", data[8], data[9], data[10]);
            log.info(" * ST Pal API Version           : {}.{}.{}", data[11], data[12], data[13]);
            log.info(" * Offset Cal / value           : {} / {} mm", offsetCal, calOffset);
            log.info(" * XTalk                        : {} MCPS", xtalk);

        } catch (I2CException e) {
            log.error("Erreur lors de la récupération des informations du capteur tinyLidar {}", deviceName);
        }
    }

    private class ReadTinyLidarException extends Exception {
        public ReadTinyLidarException(final String message) {
            super(message);
        }
    }
}
