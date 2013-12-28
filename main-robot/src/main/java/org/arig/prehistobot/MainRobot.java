package org.arig.prehistobot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.integration.spring.config.ApplicationContext;
import org.arig.robot.integration.spring.config.GPIOContext;
import org.arig.robot.integration.spring.config.I2CContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by mythril on 20/12/13.
 */
@Slf4j
public class MainRobot {

    private static ConfigurableApplicationContext ctx;

    public static void main(final String [] args) {
        final Object [] contextClasses = new Object[] {
                ApplicationContext.class,
                I2CContext.class,
                GPIOContext.class
        };

        ctx = SpringApplication.run(contextClasses, args);
        ctx.addApplicationListener(new MainRobotListener());
    }
}
