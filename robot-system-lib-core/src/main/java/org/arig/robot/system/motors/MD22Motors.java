package org.arig.robot.system.motors;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class MD22Motors.
 * 
 * @author mythril
 */
@Slf4j
public class MD22Motors extends AbstractMotors {

    /** The Constant MODE_REGISTER. */
    public static final byte MODE_REGISTER = 0x00;

    /** The Constant MOTOR1_REGISTER. */
    public static final byte MOTOR1_REGISTER = 0x01;

    /** The Constant MOTOR2_REGISTER. */
    public static final byte MOTOR2_REGISTER = 0x02;

    /** The Constant ACCEL_REGISTER. */
    public static final byte ACCEL_REGISTER = 0x03;

    /** The Constant VERSION_REGISTER. */
    public static final byte VERSION_REGISTER = 0x07;

    /** The Constant MODE_0. */
    private static final byte MODE_0 = 0; // 0 (Reverse) - 128 (Stop) - 255 (Forward)

    /** The Constant MODE_1. */
    private static final byte MODE_1 = 1; // -128 (Reverse) - 0 (Stop) - 127 (Forward)

    /** The Constant DEFAULT_MODE_VALUE. */
    private static final byte DEFAULT_MODE_VALUE = MD22Motors.MODE_0;

    /** The Constant DEFAULT_ACCEL_VALUE. */
    private static final byte DEFAULT_ACCEL_VALUE = 20;

    /** The Constant MIN_VAL_MODE_0. */
    private static final byte MIN_VAL_MODE_0 = 0;

    /** The Constant STOP_VAL_MODE_0. */
    private static final byte STOP_VAL_MODE_0 = (byte) 128;

    /** The Constant MAX_VAL_MODE_0. */
    private static final byte MAX_VAL_MODE_0 = (byte) 255;

    /** The Constant MIN_VAL_MODE_1. */
    private static final byte MIN_VAL_MODE_1 = -128;

    /** The Constant STOP_VAL_MODE_1. */
    private static final byte STOP_VAL_MODE_1 = 0;

    /** The Constant MAX_VAL_MODE_1. */
    private static final byte MAX_VAL_MODE_1 = 127;

    /** The i2c manager. */
    @Autowired
    private II2CManager i2cManager;

    /** The address. */
    private final String deviceName;

    /** The mode value. */
    private byte modeValue;

    /** The accel value. */
    private byte accelValue;

    /** The stop val. */
    private int offsetVal;

    /**
     * Instantiates a new m d22 motors.
     * 
     * @param deviceName
     *            the address
     */
    public MD22Motors(final String deviceName) {
        this(deviceName, MD22Motors.DEFAULT_MODE_VALUE, MD22Motors.DEFAULT_ACCEL_VALUE);
    }

    /**
     * Instantiates a new m d22 motors.
     * 
     * @param deviceName
     *            the address
     * @param mode
     *            the mode
     * @param accel
     *            the accel
     */
    public MD22Motors(final String deviceName, final byte mode, final byte accel) {
        super();

        this.deviceName = deviceName;
        modeValue = mode;
        accelValue = accel;

        init(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#init()
     */
    @Override
    public void init() {
        init(true);
    }

    /**
     * Inits the.
     * 
     * @param transmit
     *            the transmit
     */
    private void init(final boolean transmit) {
        prevM1 = 300;
        prevM2 = 300;

        setMode(modeValue, transmit);
        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            MD22Motors.log.error("Impossible de faire une pause", e);
        }
        setAccel(accelValue, transmit);

        if (transmit) {
            stopAll();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#moteur1(int)
     */
    @Override
    public void moteur1(final int val) {
        final byte cmd = (byte) check(val + offsetVal);
        if (cmd == prevM1) {
            return;
        }
        prevM1 = cmd;

        MD22Motors.log.info(String.format("Commande du moteur 1 : %s", cmd));
        try {
            i2cManager.sendData(deviceName, MD22Motors.MOTOR1_REGISTER, cmd);
        } catch (I2CException e) {
            log.error("Impossible d'envoyer la commande moteur 1");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#moteur2(int)
     */
    @Override
    public void moteur2(final int val) {
        final byte cmd = (byte) check(val + offsetVal);
        if (cmd == prevM2) {
            return;
        }
        prevM2 = cmd;

        MD22Motors.log.info(String.format("Commande du moteur 2 : %s", cmd));
        try {
            i2cManager.sendData(deviceName, MD22Motors.MOTOR2_REGISTER, cmd);
        } catch (I2CException e) {
            log.error("Impossible d'envoyer la commande moteur 2");
        }
    }

    /**
     * Configuration du mode de la carte MD22. Les modes 0 et 1 sont géré uniquement.
     * 
     * @param value
     *            the new mode
     */
    public void setMode(final byte value) {
        setMode(value, true);
    }

    /**
     * Sets the mode.
     * 
     * @param value
     *            the value
     * @param transmit
     *            the transmit
     */
    private void setMode(final byte value, final boolean transmit) {
        modeValue = value;
        switch (modeValue) {
            case MODE_0:
                minVal = MD22Motors.MIN_VAL_MODE_0;
                maxVal = MD22Motors.MAX_VAL_MODE_0;
                offsetVal = MD22Motors.STOP_VAL_MODE_0;
                break;

            case MODE_1:
            default:
                minVal = MD22Motors.MIN_VAL_MODE_1;
                maxVal = MD22Motors.MAX_VAL_MODE_1;
                offsetVal = MD22Motors.STOP_VAL_MODE_1;
                break;
        }

        // Set mode
        if (transmit) {
            MD22Motors.log.info(String.format("Configuration dans le mode %d (Min = %d, Max = %d, Offset = %d)", modeValue, minVal, maxVal, offsetVal));
            try {
                i2cManager.sendData(deviceName, MD22Motors.MODE_REGISTER, modeValue);
            } catch (I2CException e) {
                log.error("Impossible d'enregistrer le mode");
            }
        }
    }

    /**
     * Configuration de la valeur d'accéleration des moteurs. L'accéleration fonctionne comme suit :
     * 
     * If you require a controlled acceleration period for the attached motors to reach there ultimate speed, the MD22
     * has a register to provide this. It works by inputting a value into the acceleration register which acts as a
     * delay in the power stepping. The amount of steps is the difference between the current speed of the motors and
     * the new speed (from speed 1 and 2 registers). So if the motors were traveling at full speed in the forward
     * direction (255) and were instructed to move at full speed in reverse (0), there would be 255 steps.
     * 
     * The acceleration register contains the rate at which the motor board moves through the steps. At 0 (default) the
     * board changes the power (accelerates) at its fastest rate, each step taking 64us. When the acceleration register
     * is loaded with the Slowest setting of 255, the board will change the power output every 16.4ms.
     * 
     * So to calculate the time (in seconds) for the acceleration to complete : time = accel reg value * 64us * steps.
     * For example :
     * 
     * ----------------------------------------------------------------------------------
     * | Accel reg | Time/step | Current speed | New speed | Steps | Acceleration time |
     * ----------------------------------------------------------------------------------
     * | 0 | 0 | 0 | 255 | 255 | 0 |
     * ----------------------------------------------------------------------------------
     * | 20 | 1.28ms | 127 | 255 | 128 | 164ms |
     * ----------------------------------------------------------------------------------
     * | 50 | 3.2ms | 80 | 0 | 80 | 256ms |
     * ----------------------------------------------------------------------------------
     * | 100 | 6.4ms | 45 | 7 | 38 | 243ms |
     * ----------------------------------------------------------------------------------
     * | 150 | 9.6ms | 255 | 5 | 250 | 2.4s |
     * ----------------------------------------------------------------------------------
     * | 200 | 12.8ms | 127 | 0 | 127 | 1.63s |
     * ----------------------------------------------------------------------------------
     * | 255 | 16.32ms | 65 | 150 | 85 | 1.39s |
     * ----------------------------------------------------------------------------------
     * 
     * @param value
     *            the new accel
     */
    public void setAccel(final byte value) {
        setAccel(value, true);
    }

    /**
     * Sets the accel.
     * 
     * @param value
     *            the value
     * @param transmit
     *            the transmit
     */
    private void setAccel(byte value, final boolean transmit) {
        if (value < 0) {
            value = 0;
        }
        if (value > (byte) 255) {
            value = (byte) 255;
        }
        accelValue = value;

        // Set accelleration
        if (transmit) {
            MD22Motors.log.info(String.format("Configuration de l'acceleration : %d", accelValue));
            try {
                i2cManager.sendData(deviceName, MD22Motors.ACCEL_REGISTER, accelValue);
            } catch (I2CException e) {
                log.error("Impossible de configurer l'acceleration");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#printVersion()
     */
    @Override
    public void printVersion() {
        try {
            i2cManager.sendData(deviceName, MD22Motors.VERSION_REGISTER);
            final byte version = i2cManager.getData(deviceName);
            MD22Motors.log.info(String.format("MD22 DC Motors (V : %s)", version));
        } catch (I2CException e) {
            log.error("Erreur lors de la récupération de la version de la carte MD22", e);
        }
    }
}
