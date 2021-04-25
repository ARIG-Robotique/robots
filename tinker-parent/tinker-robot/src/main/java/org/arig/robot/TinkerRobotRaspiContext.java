package org.arig.robot;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManagerDevice;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.constants.IConstantesI2CTinker;
import org.arig.robot.constants.IConstantesServosTinker;
import org.arig.robot.system.motors.PropulsionsMD22Motors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Configuration
@Profile("raspi")
public class TinkerRobotRaspiContext {

    @Bean(destroyMethod = "close")
    public I2CBus i2cBus() throws IOException, UnsupportedBusNumberException {
        return I2CFactory.getInstance(I2CBus.BUS_1);
    }

    @Bean
    public II2CManager i2cManager(I2CBus i2cBus) throws IOException {
        final RaspiI2CManager manager = new RaspiI2CManager();

        final I2CManagerDevice<I2CDevice> pca9685 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CTinker.PCA9685_DEVICE_NAME)
                .device(i2cBus.getDevice(IConstantesI2CTinker.PCA9685_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> md22 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CTinker.MD22_DEVICE_NAME)
                .device(i2cBus.getDevice(IConstantesI2CTinker.MD22_ADDRESS))
                .build();

        manager.registerDevice(pca9685);
        manager.registerDevice(md22);

        return manager;
    }

    @Bean
    @SneakyThrows
    public PCA9685GpioProvider pca9685GpioControler(I2CBus bus) {
        final PCA9685GpioProvider gpioProvider = new PCA9685GpioProvider(bus, IConstantesI2CTinker.PCA9685_ADDRESS,
                new BigDecimal(60));

        final GpioController gpio = GpioFactory.getInstance();

        gpio.provisionPwmOutputPin(gpioProvider, IConstantesServosTinker.FOURCHE);
        gpio.provisionPwmOutputPin(gpioProvider, IConstantesServosTinker.BLOCAGE_DROITE);
        gpio.provisionPwmOutputPin(gpioProvider, IConstantesServosTinker.BLOCAGE_GAUCHE);
        gpio.provisionPwmOutputPin(gpioProvider, IConstantesServosTinker.TRANSLATEUR);

        return gpioProvider;
    }

    @Bean
    public PropulsionsMD22Motors md22Motors() {
        PropulsionsMD22Motors motors = new PropulsionsMD22Motors(IConstantesI2CTinker.MD22_DEVICE_NAME, (byte) 1, (short) 0);
        motors.assignMotors(1, 2);
        return motors;
    }
}
