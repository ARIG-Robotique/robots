package org.arig.robot.system.servos;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class SD21Servos.
 * 
 * @author GregoryDepuille
 */
@Slf4j
public class SD21Servos {

    /** The Constant SD21_VERSION_REGISTER. */
    public static final byte SD21_VERSION_REGISTER = 0x40;

    /** The address. */
    protected String deviceName;

    /** The ret code. */
    protected byte retCode;

    /** The i2c manager. */
    @Autowired
    private II2CManager i2cManager;

    /**
     * Instantiates a new s d21 servos.
     * 
     * @param deviceName
     *            the address
     */
    public SD21Servos(final String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Gets the base register. Renvoi le registre de base pour un servo. Par éxemple pour le servo 1 : 0 : SPEED
     * REGISTER 1 : LOW BYTE POSITION
     * REGISTER 2 : HIGH BYTE POSITION REGISTER
     * 
     * @param servoNb
     *            the servo nb
     * @return the base register
     */
    public static byte getBaseRegister(final byte servoNb) {
        return (byte) (servoNb * 3 - 3);
    }

    /**
     * Sets the position.
     * 
     * @param servoNb
     *            the servo nb
     * @param position
     *            the position
     */
    public void setPosition(final byte servoNb, final int position) {
        if (!checkServo(servoNb)) {
            return;
        }

        try {
            SD21Servos.log.info(String.format("Définition de la position du servo %d (Position = %d)", servoNb, position));
            i2cManager.sendData(deviceName, (byte) (SD21Servos.getBaseRegister(servoNb) + 1), (byte) (position & 0xFF), (byte) (position >> 8));
        } catch (I2CException e) {
            log.error("Erreur lors de l'envoi de la position");
        }
    }

    /**
     * Sets the speed.
     * 
     * @param servoNb
     *            the servo nb
     * @param speed
     *            the speed
     */
    public void setSpeed(final byte servoNb, final byte speed) {
        if (!checkServo(servoNb)) {
            return;
        }

        try {
            SD21Servos.log.info(String.format("Définiion de la vitesse du servo %d (Vitesse = %d)", servoNb, speed));
            i2cManager.sendData(deviceName, SD21Servos.getBaseRegister(servoNb), speed);
        } catch (I2CException e) {
            log.error("Erreur lors de l'envoi de la vitesse");
        }
    }

    /**
     * Sets the position and speed.
     * 
     * @param servoNb
     *            the servo nb
     * @param speed
     *            the speed
     * @param position
     *            the position
     */
    public void setPositionAndSpeed(final byte servoNb, final byte speed, final int position) {
        if (!checkServo(servoNb)) {
            return;
        }

        try {
            SD21Servos.log.info(String.format("Comande du servo %d (Vitesse = %d,  Position = %d)", servoNb, speed, position));
            i2cManager.sendData(deviceName, SD21Servos.getBaseRegister(servoNb), speed, (byte) (position & 0xFF), (byte) (position >> 8));
        } catch (I2CException e) {
            log.error("Erreur lors de la définition de la vitesse et de la position");
        }
    }

    /**
     * Prints the version.
     */
    public void printVersion() {
        try {
            i2cManager.sendData(deviceName, SD21Servos.SD21_VERSION_REGISTER);
            final short version = i2cManager.getData(deviceName);
            SD21Servos.log.info(String.format("SD21 ServoMotors (V : %s)", version));
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération de la version de la carte SD21");
        }
    }

    /**
     * Check servo.
     * 
     * @param servoNb
     *            the servo nb
     * @return true, if servo number are between 1 and 21. False otherwise
     */
    private boolean checkServo(final byte servoNb) {
        final boolean result = servoNb >= 1 && servoNb <= 21;
        if (!result) {
            SD21Servos.log.warn(String.format("Numéro de servo moteur invalide : %d", servoNb));
        }
        return result;
    }
}
