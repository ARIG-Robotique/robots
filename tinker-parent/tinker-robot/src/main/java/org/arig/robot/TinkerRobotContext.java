package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.listener.JoyConLeftEventListener;
import org.arig.robot.listener.JoyConRightEventListener;
import org.arig.robot.model.RobotName;
import org.arig.robot.services.IServosServices;
import org.arig.robot.services.ServosServices;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEvent;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEventListener;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConLeft;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConRight;
import org.arig.robot.system.gamepad.nintendoswitch.pro.ProController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
