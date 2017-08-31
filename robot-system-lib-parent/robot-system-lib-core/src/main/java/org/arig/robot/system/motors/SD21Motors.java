package org.arig.robot.system.motors;

import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class SD21Motors.
 *
 * @author GregoryDepuille
 */
public class SD21Motors extends AbstractPropulsionsMotors {

    private final byte motor1Register;
    private final byte motor2Register;
    private final int offsetValue;

    @Autowired
    private SD21Servos sd21;

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

    @Override
    public void init() {
        stopAll();
        sd21.setSpeed(motor1Register, (byte) 0);
        sd21.setSpeed(motor2Register, (byte) 0);
    }

    @Override
    public void moteur1(final int val) {
        final int cmd = check(val + offsetValue);
        if (cmd == prevM1) {
            return;
        }
        prevM1 = cmd;

        sd21.setPosition(motor1Register, cmd);
    }

    @Override
    public void moteur2(final int val) {
        final int cmd = check(val + offsetValue);
        if (cmd == prevM2) {
            return;
        }
        prevM2 = cmd;

        sd21.setPosition(motor2Register, cmd);
    }

    @Override
    public int getMinSpeed() {
        return minVal - offsetValue;
    }

    @Override
    public int getMaxSpeed() {
        return maxVal - offsetValue;
    }

    @Override
    protected int currentSpeedMoteur1() {
        return prevM1 - offsetValue;
    }

    @Override
    protected int currentSpeedMoteur2() {
        return prevM2 - offsetValue;
    }

    @Override
    public void printVersion() {
        sd21.printVersion();
    }
}
