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

    private byte gp2dGauche = -1;
    private byte gp2dCentre = -1;
    private byte gp2dDroite = -1;

    public void refreshSensors() {
        try {
            log.debug("Refresh sensors");

            final byte[] data = i2cManager.getData(deviceName, 4);
            arriereGauche = (data[0] & 0x01) == 0x01;
            arriereDroite = (data[0] & 0x02) == 0x02;
            gp2dGauche = data[1];
            gp2dCentre = data[2];
            gp2dDroite = data[3];

            log.debug("IO Pami sensors : In1 = {} ; In2 = {} ; GP2D1 = {} ; GP2D2 = {} ; GP2D3 = {}",
                arriereGauche, arriereDroite, gp2dGauche, gp2dCentre, gp2dDroite);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération des sensors du capteur IO PAMI {}", deviceName);
        }
    }
}
