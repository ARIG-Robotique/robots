package org.arig.robot.tinker;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.RobotName.RobotIdentification;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEventListener;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConLeft;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConRight;
import org.arig.robot.system.gamepad.nintendoswitch.pro.ProController;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.tinker.listener.JoyConLeftEventListener;
import org.arig.robot.tinker.listener.JoyConRightEventListener;
import org.arig.robot.tinker.services.ServosServices;
import org.arig.robot.tinker.services.ServosServicesImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TinkerRobotContext {

    protected RobotName robotName() {
        return RobotName.builder()
                .id(RobotIdentification.TINKER)
                .name("Tinker (JoyCon drived)")
                .version("2020 (Sail the World)")
                .build();
    }

    @Bean
    public RobotName robotNameBean() {
        return robotName();
    }

    @Bean
    public ServosServices servosServices(PCA9685GpioProvider pca9685GpioProvider) {
        return new ServosServicesImpl(pca9685GpioProvider);
    }

    @Bean
    public JoyConLeftEventListener leftEventListener(ServosServices servosServices, AbstractPropulsionsMotors motors) {
        return new JoyConLeftEventListener(servosServices, motors);
    }

    @Bean
    public JoyConRightEventListener rightEventListener(ServosServices servosServices, AbstractPropulsionsMotors motors) {
        return new JoyConRightEventListener(servosServices, motors);
    }

    @Bean
    public ControllerEventListener proEventListener(JoyConLeftEventListener left, JoyConRightEventListener right) {
        return (event) -> {
            left.handleInput(event);
            right.handleInput(event);
        };
    }

    @Bean(destroyMethod = "close")
    public JoyConLeft joyConLeft(JoyConLeftEventListener leftEventListener) {
        return new JoyConLeft(leftEventListener);
    }

    @Bean(destroyMethod = "close")
    public JoyConRight joyConRight(JoyConRightEventListener rightEventListener) {
        return new JoyConRight(rightEventListener);
    }

    @Bean(destroyMethod = "close")
    public ProController pro(ControllerEventListener proEventListener) {
        return new ProController(proEventListener);
    }
}
