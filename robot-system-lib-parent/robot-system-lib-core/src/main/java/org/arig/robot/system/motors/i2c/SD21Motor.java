package org.arig.robot.system.motors.i2c;

import org.arig.robot.system.motors.AbstractMotor;
import org.arig.robot.system.servos.i2c.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;

public class SD21Motor extends AbstractMotor {

    public static int OFFSET = 1500;
    public static int MIN = 1100;
    public static int MAX = 1900;

    private final byte motorRegister;

    @Autowired
    private SD21Servos sd21;

    public SD21Motor(final byte motorRegister) {
        super(OFFSET);

        this.motorRegister = motorRegister;

        minVal = MIN;
        maxVal = MAX;
        prev = OFFSET;
    }

    @Override
    public void init() {
        stop();
        sd21.setSpeed(motorRegister, (byte) 0);
    }

    @Override
    public void speed(int val) {
        if (this.reverse()) {
            val = -val;
        }
        final int cmd = check(val + offsetValue);
        if (cmd == prev) {
            return;
        }
        prev = cmd;

        sd21.setPosition(motorRegister, cmd);
    }

    @Override
    public void printVersion() {
        sd21.printVersion();
    }
}
