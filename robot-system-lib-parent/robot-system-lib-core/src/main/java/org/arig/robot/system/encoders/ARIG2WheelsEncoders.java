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
            double v = lectureData(deviceNameGauche);
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
            double v = lectureData(deviceNameDroit);
            if (log.isDebugEnabled()) {
                log.debug("Lecture codeur droit : {}", v);
            }
            return v;
        } catch (final I2CException e) {
            log.error("Erreur lors de la lecture du codeur droit : " + e.toString());
            return 0;
        }
    }

    /**
     * Lecture data depuis nos cartes codeur.
     * <p>
     * 1) On envoi la commande de lecture.
     * 2) On récupère un short (2 octets car int sur 2 octet avec un AVR 8 bits)
     *
     * @param deviceName the deviceName
     *
     * @return the int
     *
     * @throws I2CException
     * @see <a href="https://github.com/ARIG-Robotique/quadratic-reader">GitHub Quadratic Reader</a>
     */
    private int lectureData(final String deviceName) throws I2CException {
        try {
            i2cManager.sendData(deviceName, 2);
        } catch (I2CException e) {
            log.error("Impossible de lire la valeur codeur pour la carte " + deviceName);
            throw new I2CException("Impossible de lire la valeur codeur pour la carte " + deviceName, e);
        }

        final byte[] datas = i2cManager.getDatas(deviceName, 2);
        return ((short) ((datas[0] << 8) + (datas[1] & 0xFF)));
    }
}
