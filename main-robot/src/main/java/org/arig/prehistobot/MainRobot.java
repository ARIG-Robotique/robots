package org.arig.prehistobot;

import lombok.extern.slf4j.Slf4j;
import org.arig.prehistobot.constants.ConstantesServos;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.system.motors.AbstractMotors;
import org.arig.robot.system.motors.MD22Motors;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.security.RunAs;

/**
 * Created by mythril on 20/12/13.
 */
@Slf4j
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class MainRobot {

    public static void main(final String [] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(MainRobot.class, args);

        // Bus I2C
        II2CManager i2CManager = ctx.getBean(RaspiI2CManager.class);
        i2CManager.executeScan();

        // Moteurs propulsion
        AbstractMotors motors = ctx.getBean(MD22Motors.class);
        motors.printVersion();
        motors.init();

        // Init servos
        SD21Servos servos = ctx.getBean(SD21Servos.class);
        servos.printVersion();
        servos.setPositionAndSpeed(ConstantesServos.SERVO_BRAS_DROIT, ConstantesServos.BRAS_DROIT_HOME, ConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(ConstantesServos.SERVO_BRAS_GAUCHE, ConstantesServos.BRAS_GAUCHE_HOME, ConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(ConstantesServos.SERVO_PORTE_DROITE, ConstantesServos.PORTE_DROITE_CLOSE, ConstantesServos.SPEED_PORTE);
        servos.setPositionAndSpeed(ConstantesServos.SERVO_PORTE_GAUCHE, ConstantesServos.PORTE_GAUCHE_CLOSE, ConstantesServos.SPEED_PORTE);
    }
}
