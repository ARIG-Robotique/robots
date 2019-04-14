package org.arig.robot.system.motors;

import lombok.Setter;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class PropulsionsSD21Motors extends AbstractPropulsionsMotors implements ApplicationContextAware, InitializingBean {

    private final byte motor1Register, motor2Register;

    private SD21Motor motor1;
    private SD21Motor motor2;

    @Setter
    private ApplicationContext applicationContext;


    public PropulsionsSD21Motors(final byte motor1Register, final byte motor2Register) {
        super(1500);
        this.motor1Register = motor1Register;
        this.motor2Register = motor2Register;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        motor1 = new SD21Motor(motor1Register);
        motor2 = new SD21Motor(motor2Register);

        applicationContext.getAutowireCapableBeanFactory().autowireBean(motor1);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(motor2);
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
