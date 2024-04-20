package org.arig.robot.system.motors;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.Pin;
import org.springframework.beans.factory.annotation.Autowired;

public class PCA9685ToTB6612Motor extends AbstractMotor {

    public enum TB6612StopMode {
        BRAKE, COAST
    }

    public static int OFFSET = 0;

    private final Pin motorPin;
    private final Pin in1Pin;
    private final Pin in2Pin;
    private final TB6612StopMode stopMode;

    private Boolean prevDirection;

    @Autowired
    private PCA9685GpioProvider pca9685;

    public PCA9685ToTB6612Motor(final Pin motorPin, final Pin in1Pin, final Pin in2Pin){
        this(motorPin, in1Pin, in2Pin, TB6612StopMode.COAST);
    }

    public PCA9685ToTB6612Motor(final Pin motorPin, final Pin in1Pin, final Pin in2Pin, final TB6612StopMode stopMode) {
        super(OFFSET);

        this.motorPin = motorPin;
        this.in1Pin = in1Pin;
        this.in2Pin = in2Pin;

        this.stopMode = stopMode;

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
    public void stop() {
        prevDirection = null;
        prev = Integer.MAX_VALUE;
        pca9685.setAlwaysOff(motorPin);
        if (stopMode == TB6612StopMode.BRAKE) {
            pca9685.setAlwaysOn(in1Pin);
            pca9685.setAlwaysOn(in2Pin);
        } else {
            pca9685.setAlwaysOff(in1Pin);
            pca9685.setAlwaysOff(in2Pin);
        }
    }

    @Override
    public void speed(final int val) {
        final int cmd = check(val + offsetValue);
        if (cmd == prev) {
            return;
        }
        prev = cmd;
        pca9685.setPwm(motorPin, 0, Math.abs(cmd));

        final boolean dir = cmd > 0;
        if (prevDirection != null && dir == prevDirection) {
            return;
        }
        prevDirection = dir;

        if (dir) {
            pca9685.setAlwaysOn(in1Pin);
            pca9685.setAlwaysOff(in2Pin);
        } else {
            pca9685.setAlwaysOn(in2Pin);
            pca9685.setAlwaysOff(in1Pin);
        }
    }

    @Override
    public void printVersion() {
        // NOP
    }
}
