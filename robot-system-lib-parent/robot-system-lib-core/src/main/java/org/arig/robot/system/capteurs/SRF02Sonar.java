package org.arig.robot.system.capteurs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.filters.values.IAverage;
import org.arig.robot.filters.values.PassThroughValueAverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

/**
 * Classe d'interface avec le composant I2C SRF02 (Sonar ultrason)
 *
 * @author gdepuille
 * @see <a href="http://www.robot-electronics.co.uk/htm/srf02techI2C.htm">SRF02 documentation</a>
 */
@Slf4j
public class SRF02Sonar {

    // Write Registers
    private static final byte COMMAND_REGISTER = 0;

    // Read Registers
    private static final byte VERSION_REGISTER = COMMAND_REGISTER;
    private static final byte RANGE_HIGH_BYTE_REGISTER = 2;
    private static final byte RANGE_LOW_BYTE_REGISTER = 3;
    private static final byte AUTOTUNE_MINIMUM_HIGH_BYTE_REGISTER = 4;
    private static final byte AUTOTUNE_MINIMUM_LOW_BYTE_REGISTER = 5;

    // Commands
    private static final byte REAL_RANGE_BASE_COMMAND = 0x50; // +0 : Inches; +1 : Centimeters ; +2 : us
    private static final byte FAKE_RANGE_BASE_COMMAND = 0x56; // +0 : Inches; +1 : Centimeters ; +2 : us
    private static final byte BURST_NO_RANGING_COMMAND = 0x5C; // Juste l'echo, sans valeur (fake range sur un autre sonar)
    private static final byte FORCE_AUTOTUNE_COMMAND = 0x60;

    private static final byte ADD_CHANGE_FIRST_COMMAND = (byte) 0xA0;
    private static final byte ADD_CHANGE_SECOND_COMMAND = (byte) 0xAA;
    private static final byte ADD_CHANGE_THIRD_COMMAND = (byte) 0xA5;

    private static final int INVALID_VALUE = -1;
    private static final int READ_TIMEOUT_VALID = 70;

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum RangeUnit {

        INCHES((byte) 0),
        CENTIMETERS((byte) 1),
        MICRO_SECONDS((byte) 2);

        @Getter
        private byte value;
    }

    /**
     * The i2c manager.
     */
    @Autowired
    private II2CManager i2cManager;

    private final String deviceName;

    @Setter
    @Getter
    private RangeUnit rangeUnit = RangeUnit.CENTIMETERS;

    @Setter
    @Getter
    private boolean fakeMode = false;

    @Setter
    private IAverage<Integer> avg = new PassThroughValueAverage<>();

    public SRF02Sonar(String deviceName) {
        this.deviceName = deviceName;
    }

    public void changeAddress(byte newAddress) {
        log.info("Modification de l'addresse du Sonar {} : {}", deviceName, newAddress & 0xFF);
        try {
            i2cManager.sendData(deviceName, COMMAND_REGISTER, ADD_CHANGE_FIRST_COMMAND);
            i2cManager.sendData(deviceName, COMMAND_REGISTER, ADD_CHANGE_SECOND_COMMAND);
            i2cManager.sendData(deviceName, COMMAND_REGISTER, ADD_CHANGE_THIRD_COMMAND);
            i2cManager.sendData(deviceName, COMMAND_REGISTER, newAddress);
            log.info("Addresse modifié");
        } catch (I2CException e) {
            log.error(String.format("Erreur lors de la modification de l'addresse du Sonar %s : %s", deviceName, e.toString()), e);
        }
    }

    public void forceAutotune() {
        log.info("Demande autotune du Sonar {}", deviceName);
        try {
            i2cManager.sendData(deviceName, COMMAND_REGISTER, FORCE_AUTOTUNE_COMMAND);
        } catch (I2CException e) {
            log.error(String.format("Erreur lors de la demande d'autotune du Sonar %s : %s", deviceName, e.toString()), e);
        }
    }

    @Async
    public Future<Integer> readValue() {
        if (log.isDebugEnabled()) {
            log.debug("Lecture du Sonar {}", deviceName);
        }
        try {
            // Step 1 : Demande d'envoi de l'echo dans l'unité configuré
            i2cManager.sendData(deviceName, COMMAND_REGISTER, (byte) (((fakeMode) ? FAKE_RANGE_BASE_COMMAND : REAL_RANGE_BASE_COMMAND) + rangeUnit.getValue()));

            // Step 2 : Attente avant de pouvoir lire (la doc dit 66 ms, mais que au bout de 70 c'est toujours bon)
            Thread.sleep(READ_TIMEOUT_VALID);

            // Step 3 : On indique au sonar de se positionner sur la bit de point fort de la valeur voulu
            i2cManager.sendData(deviceName, RANGE_HIGH_BYTE_REGISTER);

            // Step 4 : On lit 2 octets
            final byte[] datas = i2cManager.getDatas(deviceName, 2);
            int res = ((short) ((datas[0] << 8) + (datas[1] & 0xFF)));
            if (log.isDebugEnabled()) {
                String unit = null;
                switch (rangeUnit) {
                    case CENTIMETERS:
                        unit = "cm";
                        break;
                    case INCHES:
                        unit = "inches";
                        break;
                    case MICRO_SECONDS:
                        unit = "µs";
                        break;
                }
                log.debug("Résultat de la lecture du sonar {} = {} {}", deviceName, res, unit);
            }
            return new AsyncResult<>(avg.average(res));
        } catch (I2CException | InterruptedException e) {
            log.error("Erreur de lecture du Sonar {} : {}", deviceName, e.toString());
            return new AsyncResult<>(INVALID_VALUE);
        }
    }

    /**
     * Prints the version.
     */
    public void printVersion() {
        try {
            i2cManager.sendData(deviceName, VERSION_REGISTER);
            final int version = i2cManager.getData(deviceName);
            log.info("Sonar {} (V : {})", deviceName, version);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération de la version du capteur SRF02 {}", deviceName);
        }
    }
}
