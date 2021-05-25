package org.arig.robot.system.vacuum;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ARIGVacuumController {

    private static final byte VERSION_REGISTER = 0x76;
    private static final byte READ_ALL_PUMPS = 0x20; // +1 => Pompe 1, etc...
    private static final byte WRITE_PUMPS_MODE = 0x30;

    private static final int NB_PUMPS = 4;

    private final String deviceName;

    @Autowired
    private II2CManager i2cManager;

    private final VacuumPumpState[] states = {
            VacuumPumpState.DISABLED,
            VacuumPumpState.DISABLED,
            VacuumPumpState.DISABLED,
            VacuumPumpState.DISABLED
    };

    private final VacuumPumpData [] pumpData = {
            new VacuumPumpData(),
            new VacuumPumpData(),
            new VacuumPumpData(),
            new VacuumPumpData()
    };

    public ARIGVacuumController(final String deviceName) {
        this.deviceName = deviceName;
    }

    public void reset() {
        disableAll();
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

    public VacuumPumpData getData(int pompeNb) {
        if (checkPompe(pompeNb)) {
            return pumpData[pompeNb - 1];
        }

        log.warn("Valeur par défaut pour la pompe {}", pompeNb);
        return new VacuumPumpData();
    }

    /**
     * Passe toute la gestion du vide en `DISABLED`.
     * Voir #disable(byte)
     */
    public void disableAll() {
        for (int idx = 0 ; idx < NB_PUMPS ; idx++) {
            states[idx] = VacuumPumpState.DISABLED;
        }

        sendToController();
    }

    /**
     * Passage à l'état `DISABLED` d'une gestion de vide.
     * Etat de haute impédance pour l'electrovanne et la pome à vide.
     *
     * La lecture des IO (ADC, TOR) est inactive.
     * @param pompeNb Numéro de pompe a piloter
     */
    public void disable(int pompeNb) {
        if (checkPompe(pompeNb)) {
            log.info("Désactivation de la pompe {}", pompeNb);
            if (states[pompeNb - 1] != VacuumPumpState.DISABLED) {
                states[pompeNb - 1] = VacuumPumpState.DISABLED;
                sendToController();
            }
        }
    }

    /**
     * La gestion du vide pour le circuit `pompeNb` est actif. Le capteur TOR déclenche la prise.
     * La conversion analogique / numérique est en marche et l'état de présence sera calculé.
     * @param pompeNb Numéro de pompe a piloter
     */
    public void on(int pompeNb) {
        if (checkPompe(pompeNb)) {
            log.info("Activation de la pompe {}", pompeNb);
            if (states[pompeNb - 1] != VacuumPumpState.ON) {
                states[pompeNb - 1] = VacuumPumpState.ON;
                sendToController();
            }
        }
    }

    /**
     * La gestion du vide pour le circuit `pompeNb` est inactif.
     * Lors du changement d'état vers ce mode l'électrovanne est ouverte afin d'injecter de l'air dans le circuit.
     * @param pompeNb Numéro de pompe a piloter
     */
    public void off(int pompeNb) {
        if (checkPompe(pompeNb)) {
            log.info("Désactivation de la pompe {}", pompeNb);
            if (states[pompeNb - 1] != VacuumPumpState.OFF) {
                states[pompeNb - 1] = VacuumPumpState.OFF;
                sendToController();
            }
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

    private void sendToController() {
        try {
            byte data = (byte) (states[0].getValue() + (states[1].getValue() << 2)
                    + (states[2].getValue() << 4) + (states[3].getValue() << 6));
            i2cManager.sendData(deviceName, WRITE_PUMPS_MODE, data);
        } catch (I2CException e) {
            log.error("Erreur de la modification du mode des pompes");
        }
    }

    private byte[] readFromController(byte register, int size) {
        try {
            i2cManager.sendData(deviceName, register);
            return i2cManager.getData(deviceName, size);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération du registre {} : {}", register, e.toString());
        }
        return new byte[size];
    }

    private boolean checkPompe(final int pompeNb) {
        final boolean result = pompeNb >= 1 && pompeNb <= NB_PUMPS;
        if (!result) {
            log.warn("Numéro de pompe invalide : {}", pompeNb);
        }
        return result;
    }
}
