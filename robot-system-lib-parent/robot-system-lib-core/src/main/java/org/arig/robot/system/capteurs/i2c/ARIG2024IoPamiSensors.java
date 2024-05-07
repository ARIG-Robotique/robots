package org.arig.robot.system.capteurs.i2c;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;

@Slf4j
@Data
@RequiredArgsConstructor
public class ARIG2024IoPamiSensors {

    private final I2CManager i2cManager;
    private final String deviceName;

    private boolean arriereGauche;
    private boolean arriereDroite;

    private short gp2d1 = -1;
    private short gp2d2 = -1;
    private short gp2d3 = -1;

    public void refreshSensors() {
        try {
            log.debug("Refresh sensors");

            final byte[] data = i2cManager.getData(deviceName, 7);
            arriereGauche = (data[0] & 0x01) == 0x01;
            arriereDroite = (data[0] & 0x02) == 0x02;
            gp2d1 = ((short) ((data[2] << 8) + (data[1] & 0xFF)));
            gp2d2 = ((short) ((data[4] << 8) + (data[3] & 0xFF)));
            gp2d3 = ((short) ((data[6] << 8) + (data[5] & 0xFF)));

            log.debug("IO Pami sensors : In1 = {} ; In2 = {} ; GP2D1 = {} ; GP2D2 = {} ; GP2D3 = {}",
                arriereGauche, arriereDroite, gp2d1, gp2d2, gp2d3);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération des sensors du capteur IO PAMI {}", deviceName);
        }
    }
}
