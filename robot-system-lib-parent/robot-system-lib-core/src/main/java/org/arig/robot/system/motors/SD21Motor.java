package org.arig.robot.system.motors;

import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;

public class SD21Motor extends AbstractMotor {

    private final byte motorRegister;

    @Autowired
    private SD21Servos sd21;

    public SD21Motor(final byte motorRegister) {
        super(1500);

        this.motorRegister = motorRegister;

        minVal = 1100;
        maxVal = 1900;
        prev = 1500;
    }

    @Override
    public void init() {
        stop();
        sd21.setSpeed(motorRegister, (byte) 0);
    }

    @Override
    public void speed(final int val) {
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
