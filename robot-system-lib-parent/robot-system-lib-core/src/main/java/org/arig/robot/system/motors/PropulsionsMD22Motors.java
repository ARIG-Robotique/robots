package org.arig.robot.system.motors;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class PropulsionsMD22Motors.
 *
 * @author gdepuille
 */
@Slf4j
public class PropulsionsMD22Motors extends AbstractPropulsionsMotors {

    private static final byte MODE_REGISTER = 0x00;
    private static final byte MOTOR1_REGISTER = 0x01;
    private static final byte MOTOR2_REGISTER = 0x02;
    private static final byte ACCEL_REGISTER = 0x03;
    private static final byte VERSION_REGISTER = 0x07;
    private static final byte MODE_0 = 0; // 0 (Reverse) - 128 (Stop) - 255 (Forward)
    private static final byte MODE_1 = 1; // -128 (Reverse) - 0 (Stop) - 127 (Forward)
    private static final byte DEFAULT_MODE_VALUE = PropulsionsMD22Motors.MODE_0;
    private static final short DEFAULT_ACCEL_VALUE = 20;

    private static final byte MIN_VAL_MODE_0 = 0;
    private static final byte STOP_VAL_MODE_0 = (byte) 128;
    private static final byte MAX_VAL_MODE_0 = (byte) 255;
    private static final byte MIN_VAL_MODE_1 = -128;
    private static final byte STOP_VAL_MODE_1 = 0;
    private static final byte MAX_VAL_MODE_1 = 127;

    @Autowired
    private I2CManager i2cManager;

    private final String deviceName;
    private byte modeValue;
    private short accelValue;

    public PropulsionsMD22Motors(final String deviceName) {
        this(deviceName, PropulsionsMD22Motors.DEFAULT_MODE_VALUE, PropulsionsMD22Motors.DEFAULT_ACCEL_VALUE);
    }

    public PropulsionsMD22Motors(final String deviceName, final byte mode, final short accel) {
        super(0);

        this.deviceName = deviceName;
        modeValue = mode;
        accelValue = accel;

        init(false);
    }

    @Override
    public void init() {
        init(true);
    }

    private void init(final boolean transmit) {
        prevM1 = 300;
        prevM2 = 300;

        setMode(modeValue, transmit);
        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            log.error("Impossible de faire une pause", e);
        }
        setAccel(accelValue, transmit);

        if (transmit) {
            stopAll();
        }
    }

    @Override
    public void speedMoteur1(final int val) {
        final byte cmd = (byte) check(val + offsetValue);
        if (cmd == prevM1) {
            return;
        }
        prevM1 = cmd;

        if (log.isDebugEnabled()) {
            log.debug("Commande du moteur 1 : {}", cmd);
        }
        try {
            i2cManager.sendData(deviceName, PropulsionsMD22Motors.MOTOR1_REGISTER, cmd);
        } catch (I2CException e) {
            log.error("Impossible d'envoyer la commande moteur 1");
        }
    }

    @Override
    public void speedMoteur2(final int val) {
        final byte cmd = (byte) check(val + offsetValue);
        if (cmd == prevM2) {
            return;
        }
        prevM2 = cmd;

        if (log.isDebugEnabled()) {
            log.debug("Commande du moteur 2 : {}", cmd);
        }
        try {
            i2cManager.sendData(deviceName, PropulsionsMD22Motors.MOTOR2_REGISTER, cmd);
        } catch (I2CException e) {
            log.error("Impossible d'envoyer la commande moteur 2");
        }
    }

    /**
     * Configuration du mode de la carte MD22. Les modes 0 et 1 sont géré uniquement.
     *
     * @param value the new mode
     */
    public void setMode(final byte value) {
        setMode(value, true);
    }

    /**
     * Sets the mode.
     *
     * @param value    the value
     * @param transmit the transmit
     */
    private void setMode(final byte value, final boolean transmit) {
        modeValue = value;
        switch (modeValue) {
            case MODE_0:
                minVal = PropulsionsMD22Motors.MIN_VAL_MODE_0;
                maxVal = PropulsionsMD22Motors.MAX_VAL_MODE_0;
                offsetValue = PropulsionsMD22Motors.STOP_VAL_MODE_0;
                break;

            case MODE_1:
            default:
                minVal = PropulsionsMD22Motors.MIN_VAL_MODE_1;
                maxVal = PropulsionsMD22Motors.MAX_VAL_MODE_1;
                offsetValue = PropulsionsMD22Motors.STOP_VAL_MODE_1;
                break;
        }

        // Set mode
        if (transmit) {
            log.info("Configuration dans le mode {} (Min = {}, Max = {}, Offset = {})", modeValue, minVal, maxVal, offsetValue);
            try {
                i2cManager.sendData(deviceName, PropulsionsMD22Motors.MODE_REGISTER, modeValue);
            } catch (I2CException e) {
                log.error("Impossible d'enregistrer le mode");
            }
        }
    }

    /**
     * Configuration de la valeur d'accéleration des moteurs. L'accéleration fonctionne comme suit :
     * <p>
     * If you require a controlled acceleration period for the attached motors to reach there ultimate speed, the MD22
     * has a register to provide this. It works by inputting a value into the acceleration register which acts as a
     * delay in the power stepping. The amount of steps is the difference between the current speed of the motors and
     * the new speed (from speed 1 and 2 registers). So if the motors were traveling at full speed in the forward
     * direction (255) and were instructed to move at full speed in reverse (0), there would be 255 steps.
     * <p>
     * The acceleration register contains the rate at which the motor board moves through the steps. At 0 (default) the
     * board changes the power (accelerates) at its fastest rate, each step taking 64us. When the acceleration register
     * is loaded with the Slowest setting of 255, the board will change the power output every 16.4ms.
     * <p>
     * So to calculate the time (in seconds) for the acceleration to complete : time = accel reg value * 64us * steps.
     * For example :
     * <p>
     * ---------------------------------------------------------------------------------
     * | Accel reg | Time/step | Current speed | New speed | Steps | Acceleration time |
     * ---------------------------------------------------------------------------------
     * | 0         | 0         | 0             | 255       | 255   | 0                 |
     * ---------------------------------------------------------------------------------
     * | 20        | 1.28ms    | 127           | 255       | 128   | 164ms             |
     * ---------------------------------------------------------------------------------
     * | 50        | 3.2ms     | 80            | 0         | 80    | 256ms             |
     * ---------------------------------------------------------------------------------
     * | 100       | 6.4ms     | 45            | 7         | 38    | 243ms             |
     * ---------------------------------------------------------------------------------
     * | 150       | 9.6ms     | 255           | 5         | 250   | 2.4s              |
     * ---------------------------------------------------------------------------------
     * | 200       | 12.8ms    | 127           | 0         | 127   | 1.63s             |
     * ---------------------------------------------------------------------------------
     * | 255       | 16.32ms   | 65            | 150       | 85    | 1.39s             |
     * ---------------------------------------------------------------------------------
     *
     * @param value the new accel
     */
    public void setAccel(final short value) {
        setAccel(value, true);
    }

    /**
     * Sets the accel.
     *
     * @param value    the value
     * @param transmit the transmit
     */
    private void setAccel(short value, final boolean transmit) {
        if (value < 0) {
            value = 0;
        }
        if (value > 255) {
            value = 255;
        }
        accelValue = value;

        // Set accelleration
        if (transmit) {
            log.info("Configuration de l'acceleration : {}", accelValue);
            try {
                i2cManager.sendData(deviceName, PropulsionsMD22Motors.ACCEL_REGISTER, (byte) accelValue);
            } catch (I2CException e) {
                log.error("Impossible de configurer l'acceleration");
            }
        }
    }

    @Override
    public void printVersion() {
        try {
            i2cManager.sendData(deviceName, PropulsionsMD22Motors.VERSION_REGISTER);
            final int version = i2cManager.getData(deviceName);
            log.info("MD22 DC Motors (V : {})", version);
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération de la version de la carte MD22", e);
        }
    }
}
