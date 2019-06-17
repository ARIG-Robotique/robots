package org.arig.robot.system.encoders;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class ARIG2WheelsEncoders.
 *
 * @author gdepuille
 */
@Slf4j
public class ARIG2WheelsEncoders extends Abstract2WheelsEncoders {

    private final String deviceNameDroit;
    private final String deviceNameGauche;

    @Autowired
    private II2CManager i2cManager;

    public ARIG2WheelsEncoders(final String deviceNameGauche, final String deviceNameDroit) {
        super("two_wheels_encoders");
        this.deviceNameGauche = deviceNameGauche;
        this.deviceNameDroit = deviceNameDroit;
    }

    @Override
    public void reset() {
        log.info("Reset carte codeur droit");
        lectureDroit();

        log.info("Reset carte codeur gauche");
        lectureGauche();
    }

    @Override
    protected double lectureGauche() {
        try {
            double v = ARIGEncoderUtils.lectureData(i2cManager, deviceNameGauche);
            if (log.isDebugEnabled()) {
                log.debug("Lecture codeur gauche : {}", v);
            }
            return v;
        } catch (final I2CException e) {
            log.error("Erreur lors de la lecture du codeur gauche : " + e.toString());
            return 0;
        }
    }

    @Override
    protected double lectureDroit() {
        try {
            double v = ARIGEncoderUtils.lectureData(i2cManager, deviceNameDroit);
            if (log.isDebugEnabled()) {
                log.debug("Lecture codeur droit : {}", v);
            }
            return v;
        } catch (final I2CException e) {
            log.error("Erreur lors de la lecture du codeur droit : " + e.toString());
            return 0;
        }
    }
}
