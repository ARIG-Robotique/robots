package org.arig.robot.config.spring;

import org.arig.robot.Ordonanceur;
import org.arig.robot.exception.RefreshPathFindingException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

/**
 * @author gdepuille on 20/12/13.
 */
public class NerellRobot extends BootifullApplication {

    public static void main(final String [] args) throws IOException, RefreshPathFindingException {
        //boot(args);
        SpringApplication.run(NerellRobot.class, args);

        Ordonanceur.getInstance().run();
    }
}
