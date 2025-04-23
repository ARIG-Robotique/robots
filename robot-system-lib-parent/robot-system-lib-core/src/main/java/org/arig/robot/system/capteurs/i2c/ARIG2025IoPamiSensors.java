package org.arig.robot.system.capteurs.i2c;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;

@Slf4j
@Data
@RequiredArgsConstructor
public class ARIG2025IoPamiSensors {

    private final I2CManager i2cManager;
    private final String deviceName;

    private boolean arriereGauche;
    private boolean arriereDroite;

    private boolean solGauche;
    private boolean solDroit;

    public void refreshSensors() {
        try {
            log.debug("Refresh sensors");

            final byte[] data = i2cManager.getData(deviceName, 1);
            arriereGauche = (data[0] & 0x01) == 0x01;
            arriereDroite = (data[0] & 0x02) == 0x02;
            solGauche = (data[0] & 0x04) == 0x04;
            solDroit = (data[0] & 0x08) == 0x08;

            log.debug("IO Pami sensors : In1 = {} ; In2 = {} ; In3 = {} ; In4 = {}",
                arriereGauche, arriereDroite, solGauche, solDroit);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération des sensors du capteur IO PAMI {}", deviceName);
        }
    }
}
