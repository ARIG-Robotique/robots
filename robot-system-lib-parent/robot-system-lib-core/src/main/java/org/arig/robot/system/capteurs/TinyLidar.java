package org.arig.robot.system.capteurs;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.filters.average.IAverage;
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
    private II2CManager i2cManager;

    private final String deviceName;

    @Setter
    private IAverage<Integer> avg = new PassThroughValueAverage<>();

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
            final byte[] datas = i2cManager.getData(deviceName, 2);
            int res = ((short) ((datas[0] << 8) + (datas[1] & 0xFF)));

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
            final byte[] datas = i2cManager.getData(deviceName, 23); // Cf docs

            final String operationMode;
            switch (datas[0]) {
                case 0x43: operationMode = "Continuous"; break;
                case 0x4c: operationMode = "Single Step / Ultra Low Power"; break;
                case 0x52: operationMode = "Real time"; break;
                default: operationMode = UNKNOWN_RESULT;
            }

            final String watchDog = (datas[14] & 0x01) == 0 ? "OFF" : "ON";

            final String led;
            switch ((datas[14] & 0x06) >> 1) {
                case 0: led = "OFF"; break;
                case 1: led = "ON"; break;
                case 2: led = "Measurement"; break;
                default: led = UNKNOWN_RESULT;
            }

            final String presetConfig;
            switch (datas[1]) {
                case 0x53: presetConfig = "High Speed"; break;
                case 0x52: presetConfig = "Long Range"; break;
                case 0x41: presetConfig = "High Accuracy"; break;
                case 0x43: presetConfig = "Custom"; break;
                case 0x54: presetConfig = "tinyLiDAR"; break;
                default: presetConfig = UNKNOWN_RESULT;
            }

            final double signalRate = ((datas[2] << 8) | (datas[3] & 0xFF)) / 65536.0 + 0.005;
            final int timingBudget = (datas[5] << 8) | (datas[6] & 0xFF);

            final int preRange = datas[7] == 14 ? 14 : 18;
            final int finalRange = datas[7] == 14 ? 10 : 14;

            final String offsetCal = ((datas[14] & 0x08) == 8) ? "Custom" : "Default";

            final int calOffset = (int) ((datas[15] << 24 | datas[16] << 16 | datas[17] << 8 | datas[18]) / 1000.0);
            final double xtalk = (datas[19] << 24 | datas[20] << 16 | datas[21] << 8 | datas[22]) / 65536.0;

            log.info("tinyLidar {} configuration :", deviceName);
            log.info(" * Current operation mode       : {}", operationMode);
            log.info(" * Watchdog                     : {}", watchDog);
            log.info(" * LED Indicator                : {}", led);
            log.info(" * Current Preset Configuration : {}", presetConfig);
            log.info(" * Signal Rate limit            : {} MCPS", signalRate);
            log.info(" * Sigma Estimate Limit         : {} mm", datas[4]);
            log.info(" * Timing budget                : {} ms", timingBudget);
            log.info(" * Pre Range VCSEL Period       : {}", preRange);
            log.info(" * Final Range VCSEL Period     : {}", finalRange);
            log.info(" * Firmware Version             : {}.{}.{}", datas[8], datas[9], datas[10]);
            log.info(" * ST Pal API Version           : {}.{}.{}", datas[11], datas[12], datas[13]);
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
