package org.arig.robot.system.encoders;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ARIGEncoder extends AbstractEncoder {

    private final String deviceName;

    @Autowired
    private II2CManager i2cManager;

    public ARIGEncoder(final String deviceName) {
        super(deviceName + "_encoder");
        this.deviceName = deviceName;
    }

    @Override
    public void reset() {
        log.info("Reset carte codeur " + deviceName);
        lecture();
    }

    @Override
    protected double lecture() {
        try {
            double v = ARIGEncoderUtils.lectureData(i2cManager, deviceName);
            if (log.isDebugEnabled()) {
                log.debug("Lecture codeur {} : {}", deviceName, v);
            }
            return v;
        } catch (final I2CException e) {
            log.error("Erreur lors de la lecture du codeur {} : {}", deviceName, e.toString());
            return 0;
        }
    }

}
