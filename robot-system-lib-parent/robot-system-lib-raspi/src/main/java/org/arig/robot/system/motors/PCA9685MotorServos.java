package org.arig.robot.system.motors;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.Pin;
import org.springframework.beans.factory.annotation.Autowired;

public class PCA9685MotorServos extends AbstractMotor {

    private final Pin motorPin;

    @Autowired
    private PCA9685GpioProvider pca9685;

    public PCA9685MotorServos(final Pin motorPin) {
        super(1500);

        this.motorPin = motorPin;

        minVal = 1100;
        maxVal = 1900;
        prev = 1500;
    }

    @Override
    public void init() {
        stop();
    }

    @Override
    public void speed(final int val) {
        final int cmd = check(val + offsetValue);
        if (cmd == prev) {
            return;
        }
        prev = cmd;

        pca9685.setPwm(motorPin, cmd);
    }

    @Override
    public void printVersion() {
        // NOP
    }
}
