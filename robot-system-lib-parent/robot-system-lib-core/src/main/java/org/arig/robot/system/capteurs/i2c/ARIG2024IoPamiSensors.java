package org.arig.robot.system.capteurs.i2c;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Data
@RequiredArgsConstructor
public class ARIG2024IoPamiSensors {

    private final I2CManager i2cManager;
    private final String deviceName;

    private boolean input1;
    private boolean input2;

    private byte gp2d1 = -1;
    private byte gp2d2 = -1;
    private byte gp2d3 = -1;

    public void refreshSensors() {
        try {
            log.debug("Refresh sensors");
            final byte[] data = i2cManager.getData(deviceName, 4);
            input1 = (data[0] & 0x01) == 0x01;
            input2 = (data[0] & 0x02) == 0x02;
            gp2d1 = data[1];
            gp2d2 = data[2];
            gp2d3 = data[3];

            log.debug("IO Pami sensors : In1 = {} ; In2 = {} ; GP2D1 = {} ; GP2D2 = {} ; GP2D3 = {}",
                    input1, input2, gp2d1, gp2d2, gp2d3);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération des sensors du capteur IO PAMI {}", deviceName);
        }
    }
}
