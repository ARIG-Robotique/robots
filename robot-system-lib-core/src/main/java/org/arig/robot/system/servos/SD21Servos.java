package org.arig.robot.system.servos;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SD21Servos.
 * 
 * @author GregoryDepuille
 */
@Slf4j
public class SD21Servos {

    /** The Constant SD21_VERSION_REGISTER. */
    private static final byte SD21_VERSION_REGISTER = 0x40;

    /** The address. */
    protected byte address;

    /** The ret code. */
    protected byte retCode;

    /**
     * Instantiates a new s d21 servos.
     *
     * @param address the address
     */
    public SD21Servos(final byte address) {
        this.address = address;
    }

    /**
     * Sets the position.
     *
     * @param servoNb the servo nb
     * @param position the position
     */
    public void setPosition(final byte servoNb, final int position) {

    }

    /**
     * Sets the speed.
     *
     * @param servoNb the servo nb
     * @param speed the speed
     */
    public void setSpeed(final byte servoNb, final byte speed) {

    }

    /**
     * Sets the position and speed.
     *
     * @param servoNb the servo nb
     * @param speed the speed
     * @param position the position
     */
    public void setPositionAndSpeed(final byte servoNb, final byte speed, final int position) {

    }


    /**
     * Prints the version.
     */
    public void printVersion() {

    }

    /**
     * Gets the base register.
     *
     * @param servoNb the servo nb
     * @return the base register
     */
    protected byte getBaseRegister(final byte servoNb) {

    }

    /**
     * Check servo.
     *
     * @param servoNb the servo nb
     * @return true, if successful
     */
    private boolean checkServo(final byte servoNb) {

    }
}
