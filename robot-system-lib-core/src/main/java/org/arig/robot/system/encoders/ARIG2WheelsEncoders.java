package org.arig.robot.system.encoders;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.AbstractI2CManager;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class ARIG2WheelsEncoders.
 * 
 * @author mythril
 */
@Slf4j
public class ARIG2WheelsEncoders extends Abstract2WheelsEncoders {

    /** The address droit. */
    private final String deviceNameDroit;

    /** The address gauche. */
    private final String deviceNameGauche;

    /** The i2c manager. */
    @Autowired
    private II2CManager i2cManager;

    /**
     * Instantiates a new aRIG encoders.
     * 
     * @param deviceNameGauche
     *            the address gauche
     * @param deviceNameDroit
     *            the address droit
     */
    public ARIG2WheelsEncoders(final String deviceNameGauche, final String deviceNameDroit) {
        this.deviceNameGauche = deviceNameGauche;
        this.deviceNameDroit = deviceNameDroit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.encoders.AbstractEncoders#reset()
     */
    @Override
    public void reset() {
        ARIG2WheelsEncoders.log.info("Reset carte codeur droit");
        lectureDroit();

        ARIG2WheelsEncoders.log.info("Reset carte codeur gauche");
        lectureGauche();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.encoders.AbstractEncoders#lectureGauche()
     */
    @Override
    protected double lectureGauche() {
        try {
            return lectureData(deviceNameGauche);
        } catch (final I2CException e) {
            ARIG2WheelsEncoders.log.error("Erreur lors de la lecture du codeur gauche : " + e.toString());
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.encoders.AbstractEncoders#lectureDroit()
     */
    @Override
    protected double lectureDroit() {
        try {
            return lectureData(deviceNameDroit);
        } catch (final I2CException e) {
            ARIG2WheelsEncoders.log.error("Erreur lors de la lecture du codeur droit : " + e.toString());
            return 0;
        }
    }

    /**
     * Lecture data depuis nos cartes codeur Arduino. {@link https://www.gitorious.org/arig-association/quadratic-reader/}
     * 
     * 1) On envoi la commande de lecture.
     * 2) On récupère 2 octets (int sur 2 octet avec un AVR 8 bits)
     * 
     * @param deviceName
     *            the deviceName
     * @return the int
     * @throws I2CException
     */
    private int lectureData(final String deviceName) throws I2CException {
        try {
            i2cManager.sendData(deviceName, 2);
        } catch (I2CException e) {
            log.error("Impossible de lire la valeur codeur pour la carte " + deviceName);
            throw new I2CException("Impossible de lire la valeur codeur pour la carte " + deviceName);
        }

        int value = 0;
        final byte[] datas = i2cManager.getDatas(deviceName, 2);
        value = datas[0] << 8;
        value += datas[1];

        ARIG2WheelsEncoders.log.info(String.format("Lecture de la valeur %s pour le codeur %d", value, deviceName));
        return value;
    }
}
