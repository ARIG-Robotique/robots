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

    private boolean input1;
    private boolean input2;
    private boolean input3;
    private boolean input4;
    private boolean input5;
    private boolean input6;
    private boolean input7;

    public void refreshSensors() {
        try {
            log.debug("Refresh sensors");

            final byte[] data = i2cManager.getData(deviceName, 1);
            input1 = (data[0] & 0x01) == 0x01;
            input2 = (data[0] & 0x02) == 0x02;
            input3 = (data[0] & 0x04) == 0x04;
            input4 = (data[0] & 0x08) == 0x08;
            input5 = (data[0] & 0x10) == 0x10;
            input6 = (data[0] & 0x20) == 0x20;
            input7 = (data[0] & 0x40) == 0x40;


            log.debug("IO Pami sensors : In1 = {} ; In2 = {} ; In3 = {} ; In4 = {}, In5 = {} ; In6 = {} ; In7 = {}",
                    input1, input2, input3, input4, input5, input6, input7);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération des sensors du capteur IO PAMI {}", deviceName);
        }
    }
}
