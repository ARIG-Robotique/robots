package org.arig.robot.system.vacuum;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ARIGVacuumController extends AbstractARIGVacuumController {

    private static final byte VERSION_REGISTER = 0x76;
    private static final byte READ_ALL_PUMPS = 0x20; // +1 => Pompe 1, etc...
    private static final byte WRITE_PUMPS_MODE = 0x30;

    private final String deviceName;

    private final ReentrantLock accessLock = new ReentrantLock(true);

    @Autowired
    private I2CManager i2cManager;

    public ARIGVacuumController(final String deviceName) {
        this.deviceName = deviceName;
    }

    public void readAllValues() {
        log.debug("Lecture de toutes les pompes");
        byte[] data = readFromController(READ_ALL_PUMPS, 8);
        for (int idx = 0, bits = 0; idx < NB_PUMPS; idx++, bits += 2) {
            pumpData[idx].vacuum(((data[bits] & 0x0F) << 8) + (data[bits + 1] & 0xFF));
            pumpData[idx].presence((data[bits] >> 7 & 0x01) == 1);
            pumpData[idx].tor((data[bits] >> 6 & 0x01) == 1);
        }
    }

    public void readData(byte pompeNb) {
        if (checkPompe(pompeNb)) {
            log.debug("Lecture de la pompe {}", pompeNb);
            byte[] data = readFromController((byte) (READ_ALL_PUMPS + pompeNb), 2);
            pumpData[pompeNb - 1].vacuum(((data[0] & 0x0F) << 8) + (data[1] & 0xFF));
            pumpData[pompeNb - 1].presence((data[0] >> 7 & 0x01) == 1);
            pumpData[pompeNb - 1].tor((data[0] >> 6 & 0x01) == 1);
        }
    }

    public void printVersion() {
        execWithLock("printVersion", () -> {
            try {
                i2cManager.sendData(deviceName, VERSION_REGISTER, VERSION_REGISTER);
                final byte[] data = i2cManager.getData(deviceName, 19);
                final String version = new String(data, StandardCharsets.UTF_8);
                log.info("ARIG Vacuum Controller version {}, {} pompes", version, NB_PUMPS);
            } catch (I2CException e) {
                log.error("Erreur lors de la récupération de la version de la carte ARIG Vacuum controller");
            }
            return null;
        });
    }

    protected void sendToController() {
        execWithLock("sendToController", () -> {
            int nbTry = 0;
            do {
                try {
                    byte data = (byte) (states[0].getValue() + (states[1].getValue() << 2)
                            + (states[2].getValue() << 4) + (states[3].getValue() << 6));
                    i2cManager.sendData(deviceName, WRITE_PUMPS_MODE, data);
                } catch (I2CException e) {
                    log.error("Erreur de la modification du mode des pompes (essai {}/3) : {}", nbTry, e.toString());
                    if (nbTry < 2) {
                        ThreadUtils.sleep(1);
                    }
                }
            } while (nbTry++ < 3);
            return null;
        });
    }

    protected byte[] readFromController(byte register, int size) {
        return execWithLock("readFromController", () -> {
            int nbTry = 0;
            do {
                try {
                    i2cManager.sendData(deviceName, register, register);
                    return i2cManager.getData(deviceName, size);
                } catch (I2CException e) {
                    log.error("Erreur lors de la récupération du registre {} (essai {}/3) : {}", register, nbTry, e.toString());
                    if (nbTry < 2) {
                        ThreadUtils.sleep(1);
                    }
                }
            } while (nbTry++ < 3);
            return new byte[size];
        });
    }

    private <T> T execWithLock(String action, Callable<T> callable) {
        try {
            if (accessLock.tryLock(20, TimeUnit.MILLISECONDS)) {
                try {
                    return callable.call();
                } finally {
                    accessLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.warn("Failed locking ARIGVacuumController {} : {}", action, e.toString());
            throw new RuntimeException("Could not obtain an access-lock!", e);
        } catch (Exception e) { // unexpected exceptions
            log.error("Unexpected exception while executing {} : {}", action, e.toString());
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Could not obtain an access-lock!");
    }
}
