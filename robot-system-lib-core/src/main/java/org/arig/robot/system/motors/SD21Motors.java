package org.arig.robot.system.motors;

import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class SD21Motors.
 * 
 * @author GregoryDepuille
 */
public class SD21Motors extends AbstractPropulsionsMotors {

    /** The motor1 register. */
    private final byte motor1Register;

    /** The motor2 register. */
    private final byte motor2Register;

    /** The offset value. */
    private final int offsetValue;

    /** The sd21. */
    @Autowired
    private SD21Servos sd21;

    /**
     * Instantiates a new s d21 motors.
     */
    public SD21Motors(final byte motor1Register, final byte motor2Register) {
        super();

        this.motor1Register = motor1Register;
        this.motor2Register = motor2Register;

        minVal = 1100;
        maxVal = 1900;

        prevM1 = 1500;
        prevM2 = 1500;

        offsetValue = 1500;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#init()
     */
    @Override
    public void init() {
        stopAll();
        sd21.setSpeed(motor1Register, (byte) 0);
        sd21.setSpeed(motor2Register, (byte) 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#moteur1(int)
     */
    @Override
    public void moteur1(final int val) {
        final int cmd = check(val + offsetValue);
        if (cmd == prevM1) {
            return;
        }
        prevM1 = cmd;

        sd21.setPosition(motor1Register, cmd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#moteur2(int)
     */
    @Override
    public void moteur2(final int val) {
        final int cmd = check(val + offsetValue);
        if (cmd == prevM2) {
            return;
        }
        prevM2 = cmd;

        sd21.setPosition(motor2Register, cmd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#printVersion()
     */
    @Override
    public void printVersion() {
        sd21.printVersion();
    }
}
