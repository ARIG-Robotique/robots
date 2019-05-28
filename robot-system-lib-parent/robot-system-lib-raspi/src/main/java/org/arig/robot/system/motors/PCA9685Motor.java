package org.arig.robot.system.motors;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.Pin;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class PCA9685Motor extends AbstractMotor {

    private final Pin motorPin;
    private final Pin directionPin;

    private Boolean prevDirection;

    @Autowired
    private PCA9685GpioProvider pca9685;

    public PCA9685Motor(final Pin motorPin, final Pin directionPin) {
        super(0);

        this.motorPin = motorPin;
        this.directionPin = directionPin;

        maxVal = PCA9685GpioProvider.PWM_STEPS - 1;
        minVal = -maxVal;
        prev = Integer.MAX_VALUE;
        prevDirection = null;
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
        int pwm = Math.abs(cmd);
        if (pwm != 0) {
            pca9685.setPwm(motorPin, 0, pwm);
        } else {
            pca9685.setAlwaysOff(motorPin);
        }

        final boolean dir = cmd > 0;
        if (prevDirection != null && dir == prevDirection) {
            return;
        }
        prevDirection = dir;

        if (dir) {
            pca9685.setAlwaysOn(directionPin);
        } else {
            pca9685.setAlwaysOff(directionPin);
        }
    }

    @Override
    public void printVersion() {
        // NOP
    }
}
