package org.arig.prehistobot.config.spring;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import org.arig.prehistobot.constants.IConstantesI2C;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.system.motors.AbstractMotors;
import org.arig.robot.system.motors.SD21Motors;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

/**
 * Created by mythril on 21/12/13.
 */
@Configuration
@Profile("raspi")
public class I2CContext {

    @Bean
    public I2CBus i2cBus() throws IOException {
        return I2CFactory.getInstance(I2CBus.BUS_1);
    }

    @Bean
    public II2CManager i2cManager(I2CBus i2cBus) throws I2CException, IOException {
        final RaspiI2CManager manager = new RaspiI2CManager(i2cBus);
        manager.registerDevice(IConstantesI2C.SERVO_DEVICE_NAME, IConstantesI2C.SD21_ADDRESS);
        //manager.registerDevice(IConstantesI2C.PROPULSION_DEVICE_NAME, IConstantesI2C.MD22_ADDRESS);
        manager.registerDevice(IConstantesI2C.CODEUR_MOTEUR_DROIT, IConstantesI2C.CODEUR_DROIT_ADDRESS);
        manager.registerDevice(IConstantesI2C.CODEUR_MOTEUR_GAUCHE, IConstantesI2C.CODEUR_GAUCHE_ADDRESS);

        return manager;
    }

    @Bean
    public AbstractMotors motors() {
        // Configuration de la carte moteur propulsion.
        /*
        MD22Motors md22 = new MD22Motors(IConstantesI2C.PROPULSION_DEVICE_NAME, (byte) 0, (byte) 0);
        md22.assignMotors(2, 1);
        return md22;
        */

        SD21Motors sd21 = new SD21Motors((byte) 13, (byte) 14);
        sd21.assignMotors(2, 1);
        return sd21;
    }

    @Bean
    public SD21Servos servos() {
        return new SD21Servos(IConstantesI2C.SERVO_DEVICE_NAME);
    }

    @Bean
    public ARIG2WheelsEncoders encoders() {
        return new ARIG2WheelsEncoders(IConstantesI2C.CODEUR_MOTEUR_GAUCHE, IConstantesI2C.CODEUR_MOTEUR_DROIT);
    }
}
