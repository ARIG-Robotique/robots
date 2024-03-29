package org.arig.robot.system.servos;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SD21Servos extends AbstractServos {

    private static final byte VERSION_REGISTER = 0x40;
    private static final byte BATTERY_VOLTS_REGISTER = 0x41;
    private static final int NB_SERVOS = 21;

    @Getter
    @Accessors(fluent = true)
    private final String deviceName;

    @Autowired
    private I2CManager i2cManager;

    public SD21Servos() {
        this("SD21");
    }

    /**
     * Instantiates a new s d21 servos.
     *
     * @param deviceName the address
     */
    public SD21Servos(final String deviceName) {
        super(NB_SERVOS);
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
    @Override
    protected void setPositionImpl(final byte servoNb, final int position) {
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
    @Override
    protected void setSpeedImpl(final byte servoNb, final byte speed) {
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
    @Override
    protected void setPositionAndSpeedImpl(final byte servoNb, final int position, final byte speed) {
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
     * Prints the version.
     */
    @Override
    public void printVersion() {
        try {
            i2cManager.sendData(deviceName, SD21Servos.VERSION_REGISTER);
            final int version = i2cManager.getData(deviceName);
            log.info("Carte SD21 {} version {} ({} servos)", deviceName, version, NB_SERVOS);
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
}
