package org.arig.robot.system.servos;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SD21Servos implements InitializingBean {

    private static final byte VERSION_REGISTER = 0x40;
    private static final byte BATTERY_VOLTS_REGISTER = 0x41;
    private static final int NB_SERVOS = 21;

    protected String deviceName;

    @Autowired
    private I2CManager i2cManager;

    private Map<Byte, Integer> lastPositions = new HashMap<>(21);
    private Map<Byte, Byte> lastSpeed = new HashMap<>(21);

    @Override
    public void afterPropertiesSet() throws Exception {
        for (byte i = 1; i <= NB_SERVOS; i++) {
            lastPositions.put(i, 1500);
            lastSpeed.put(i, (byte) 0);
        }
    }

    public SD21Servos() {
        this("SD21");
    }

    /**
     * Instantiates a new s d21 servos.
     *
     * @param deviceName the address
     */
    public SD21Servos(final String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Gets the base register. Renvoi le registre de base pour un servo. Par éxemple pour le servo 1 : 0 : SPEED
     * REGISTER 1 : LOW BYTE POSITION
     * REGISTER 2 : HIGH BYTE POSITION REGISTER
     *
     * @param servoNb the servo nb
     * @return the base register
     */
    public static byte getBaseRegister(final byte servoNb) {
        return (byte) (servoNb * 3 - 3);
    }

    /**
     * Sets the position.
     *
     * @param servoNb  the servo nb
     * @param position the position
     */
    public void setPosition(final byte servoNb, final int position) {
        if (!checkServo(servoNb)) {
            return;
        }

        if (getPosition(servoNb) == position) {
            return;
        }

        lastPositions.put(servoNb, position);

        try {
            if (log.isDebugEnabled()) {
                log.debug("Définition de la position du servo {} (Position = {})", servoNb, position);
            }
            i2cManager.sendData(deviceName, (byte) (SD21Servos.getBaseRegister(servoNb) + 1), (byte) (position & 0xFF), (byte) (position >> 8));
        } catch (I2CException e) {
            log.error("Erreur lors de l'envoi de la position, servo: {}, position: {}", servoNb, position);
        }
    }

    /**
     * Sets the speed.
     *
     * @param servoNb the servo nb
     * @param speed   the speed
     */
    public void setSpeed(final byte servoNb, final byte speed) {
        if (!checkServo(servoNb)) {
            return;
        }

        if (getSpeed(servoNb) == speed) {
            return;
        }

        lastSpeed.put(servoNb, speed);

        try {
            log.info(String.format("Définiion de la vitesse du servo %d (Vitesse = %d)", servoNb, speed));
            i2cManager.sendData(deviceName, SD21Servos.getBaseRegister(servoNb), speed);
        } catch (I2CException e) {
            log.error("Erreur lors de l'envoi de la vitesse");
        }
    }

    /**
     * Sets the position and speed.
     *
     * @param servoNb  the servo nb
     * @param speed    the speed
     * @param position the position
     */
    public void setPositionAndSpeed(final byte servoNb, final int position, final byte speed) {
        if (!checkServo(servoNb)) {
            return;
        }

        if (getPosition(servoNb) == position && getSpeed(servoNb) == speed) {
            return;
        }

        lastPositions.put(servoNb, position);
        lastSpeed.put(servoNb, speed);

        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Comande du servo %d (Vitesse = %d,  Position = %d)", servoNb, speed, position));
            }
            i2cManager.sendData(deviceName, SD21Servos.getBaseRegister(servoNb), speed, (byte) (position & 0xFF), (byte) (position >> 8));
        } catch (I2CException e) {
            log.error("Erreur lors de la définition de la vitesse et de la position");
        }
    }

    /**
     * Get the last position of servo
     *
     * @param servoNb Numero du servo
     * @return La dernière position du servo
     */
    public int getPosition(final byte servoNb) {
        if (!checkServo(servoNb)) {
            return -1;
        }

        return lastPositions.get(servoNb);
    }

    /**
     * Get the last speed of servo
     *
     * @param servoNb Numero du servo
     * @return La dernière vitesse du servo
     */
    public int getSpeed(final byte servoNb) {
        if (!checkServo(servoNb)) {
            return -1;
        }

        return lastSpeed.get(servoNb);
    }

    /**
     * Prints the version.
     */
    public void printVersion() {
        try {
            i2cManager.sendData(deviceName, SD21Servos.VERSION_REGISTER);
            final int version = i2cManager.getData(deviceName);
            log.info("SD21 ServoMotors (V : {})", version);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération de la version de la carte SD21");
        }
    }

    public double getTension() {
        try {
            i2cManager.sendData(deviceName, SD21Servos.BATTERY_VOLTS_REGISTER);

            final byte[] rawVolts = i2cManager.getData(deviceName, 1);

            final double volts;

            if (rawVolts[0] >= 0) {
                volts = rawVolts[0] * 0.039; // A battery voltage of 7.2v will read about 184. 6v will read about 154.
            } else {
                volts = (255 + rawVolts[0]) * 0.039;
            }
            return volts;
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération de la tension de la carte SD21");
        }

        return -1;
    }

    /**
     * Check servo.
     *
     * @param servoNb the servo nb
     * @return true, if servo number are between 1 and 21. False otherwise
     */
    private boolean checkServo(final byte servoNb) {
        final boolean result = servoNb >= 1 && servoNb <= NB_SERVOS;
        if (!result) {
            log.warn("Numéro de servo moteur invalide : {}", servoNb);
        }
        return result;
    }

}
