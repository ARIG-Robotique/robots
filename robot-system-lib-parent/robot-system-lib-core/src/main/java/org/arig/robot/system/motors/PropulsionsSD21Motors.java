package org.arig.robot.system.motors;

import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;

public class PropulsionsSD21Motors extends AbstractPropulsionsMotors {

    private final SD21Motor motor1;
    private final SD21Motor motor2;


    public PropulsionsSD21Motors(final byte motor1Register, final byte motor2Register) {
        super(1500);
        motor1 = new SD21Motor(motor1Register);
        motor2 = new SD21Motor(motor2Register);
    }

    @Override
    public void init() {
        motor1.init();
        motor2.init();
    }

    @Override
    public void speedMoteur1(final int val) {
        motor1.speed(val);
    }

    @Override
    public void speedMoteur2(final int val) {
        motor2.speed(val);
    }

    @Override
    public int getMinSpeed() {
        return motor1.getMinSpeed();
    }

    @Override
    public int getMaxSpeed() {
        return motor1.getMaxSpeed();
    }

    @Override
    protected int currentSpeedMoteur1() {
        return motor1.currentSpeed();
    }

    @Override
    protected int currentSpeedMoteur2() {
        return motor2.currentSpeed();
    }

    @Override
    public void printVersion() {
        motor1.printVersion();
    }
}
