package org.arig.robot.config.spring;

import org.arig.robot.Ordonanceur;
import org.arig.robot.constants.IConstantesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

/**
 * @author gdepuille on 20/12/13.
 */
@SpringBootApplication
public class NerellRobot {

    public static void main(final String [] args) throws IOException {
        //boot(args);
        System.setProperty(IConstantesConfig.keyExecutionId, "0");
        ConfigurableApplicationContext ctx = SpringApplication.run(NerellRobot.class, args);

        Ordonanceur.getInstance().run();

        ctx.close();
    }
}
