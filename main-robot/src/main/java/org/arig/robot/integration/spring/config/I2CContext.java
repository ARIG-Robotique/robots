package org.arig.robot.integration.spring.config;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import org.arig.prehistobot.constants.ConstantesI2C;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.system.motors.MD22Motors;
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
    public II2CManager i2cManager() throws I2CException, IOException {
        final RaspiI2CManager manager = new RaspiI2CManager(i2cBus());
        manager.registerDevice(ConstantesI2C.SERVO_DEVICE_NAME, ConstantesI2C.SD21_ADDRESS);
        manager.registerDevice(ConstantesI2C.PROPULSION_DEVICE_NAME, ConstantesI2C.MD22_ADDRESS);

        return manager;
    }

    @Bean
    public MD22Motors motors() {
        MD22Motors md22 = new MD22Motors(ConstantesI2C.PROPULSION_DEVICE_NAME);
        md22.assignMotors(1, 2);

        return md22;
    }

    @Bean
    public SD21Servos servos() {
        return new SD21Servos(ConstantesI2C.SERVO_DEVICE_NAME);
    }
}

