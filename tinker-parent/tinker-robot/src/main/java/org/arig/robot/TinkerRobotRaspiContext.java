package org.arig.robot;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.exception.I2CException;
import org.arig.robot.listener.JoyConLeftEventListener;
import org.arig.robot.listener.JoyConRightEventListener;
import org.arig.robot.model.RobotName;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConLeft;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConRight;
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
    public II2CManager i2cManager(I2CBus i2cBus) throws I2CException {
        final RaspiI2CManager manager = new RaspiI2CManager(i2cBus);
        manager.registerDevice(IConstantesI2C.PCA9685_DEVICE_NAME, IConstantesI2C.PCA9685_ADDRESS);

        return manager;
    }

    @Bean
    @SneakyThrows
    public PCA9685GpioProvider pca9685GpioControler(I2CBus bus) {
        final PCA9685GpioProvider gpioProvider = new PCA9685GpioProvider(bus, IConstantesI2C.PCA9685_ADDRESS, PCA9685GpioProvider.ANALOG_SERVO_FREQUENCY);

        final GpioController gpio = GpioFactory.getInstance();

        gpio.provisionPwmOutputPin(gpioProvider, IConstantesServos.FOURCHE);
        gpio.provisionPwmOutputPin(gpioProvider, IConstantesServos.BLOCAGE_DROITE);
        gpio.provisionPwmOutputPin(gpioProvider, IConstantesServos.BLOCAGE_GAUCHE);
        gpio.provisionPwmOutputPin(gpioProvider, IConstantesServos.TRANSLATEUR);

        return gpioProvider;
    }
}
