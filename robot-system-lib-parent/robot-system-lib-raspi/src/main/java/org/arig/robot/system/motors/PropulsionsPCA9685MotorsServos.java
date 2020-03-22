package org.arig.robot.system.motors;

import com.pi4j.io.gpio.Pin;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * NON TESTE
 */
public class PropulsionsPCA9685MotorsServos extends AbstractPropulsionsMotors implements ApplicationContextAware, InitializingBean {

    private PCA9685MotorServos motor1;
    private PCA9685MotorServos motor2;

    @Setter
    private ApplicationContext applicationContext;

    public PropulsionsPCA9685MotorsServos(final Pin motor1Pin, final Pin motor2Pin) {
        super(PCA9685MotorServos.OFFSET);
        motor1 = new PCA9685MotorServos(motor1Pin);
        motor2 = new PCA9685MotorServos(motor2Pin);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
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
