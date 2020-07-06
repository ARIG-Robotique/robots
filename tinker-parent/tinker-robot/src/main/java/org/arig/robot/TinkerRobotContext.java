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
import org.arig.robot.exception.I2CException;
import org.arig.robot.listener.JoyConLeftEventListener;
import org.arig.robot.listener.JoyConRightEventListener;
import org.arig.robot.model.RobotName;
import org.arig.robot.services.IServosServices;
import org.arig.robot.services.ServosServices;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyCon;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConButton;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConEventListener;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConLeft;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConRight;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Configuration
public class TinkerRobotContext {

    protected RobotName robotName() {
        return new RobotName().name("Tinker (JoyCon drived)").version("2020 (Sail the World)");
    }

    @Bean
    public RobotName robotNameBean() {
        return robotName();
    }

    @Bean
    public IServosServices servosServices() {
        return new ServosServices();
    }

    @Bean
    public JoyConLeftEventListener leftEventListener(IServosServices servosServices) {
        return new JoyConLeftEventListener(servosServices);
    }

    @Bean
    public JoyConRightEventListener rightEventListener(IServosServices servosServices) {
        return new JoyConRightEventListener(servosServices);
    }

    @Bean(destroyMethod = "close")
    public JoyConLeft joyConLeft(JoyConLeftEventListener e) {
        return new JoyConLeft(e);
    }

    @Bean(destroyMethod = "close")
    public JoyConRight joyConRight(JoyConRightEventListener e) {
        return new JoyConRight(e);
    }
}
