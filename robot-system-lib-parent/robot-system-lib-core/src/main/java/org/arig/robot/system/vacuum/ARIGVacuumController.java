package org.arig.robot.system.vacuum;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ARIGVacuumController extends AbstractARIGVacuumController {

    private static final byte VERSION_REGISTER = 0x76;
    private static final byte READ_ALL_PUMPS = 0x20; // +1 => Pompe 1, etc...
    private static final byte WRITE_PUMPS_MODE = 0x30;

    private final String deviceName;

    @Autowired
    private II2CManager i2cManager;

    public ARIGVacuumController(final String deviceName) {
        this.deviceName = deviceName;
    }

    public void readAllValues() {
        log.debug("Lecture de toutes les pompes");
        byte[] data = readFromController(READ_ALL_PUMPS, 8);
        for (int idx = 0 , bits = 0 ; idx < NB_PUMPS ; idx++, bits += 2) {
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
        try {
            i2cManager.sendData(deviceName, VERSION_REGISTER);
            final byte[] data = i2cManager.getData(deviceName, 10);
            final String version = new String(data, StandardCharsets.UTF_8);
            log.info("ARIG Vacuum Controller version {}, {} pompes", version, NB_PUMPS);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération de la version de la carte ARIG Vacuum controller");
        }
    }

    protected void sendToController() {
        try {
            byte data = (byte) (states[0].getValue() + (states[1].getValue() << 2)
                    + (states[2].getValue() << 4) + (states[3].getValue() << 6));
            i2cManager.sendData(deviceName, WRITE_PUMPS_MODE, data);
        } catch (I2CException e) {
            log.error("Erreur de la modification du mode des pompes");
        }
    }

    protected byte[] readFromController(byte register, int size) {
        try {
            i2cManager.sendData(deviceName, register);
            return i2cManager.getData(deviceName, size);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération du registre {} : {}", register, e.toString());
        }
        return new byte[size];
    }

}
