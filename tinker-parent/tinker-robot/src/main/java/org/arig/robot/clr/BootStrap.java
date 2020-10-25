package org.arig.robot.clr;

import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootStrap implements CommandLineRunner {

    @Autowired
    private AbstractPropulsionsMotors motors;

    @Override
    public void run(final String... args) throws Exception {
        motors.init();
        motors.printVersion();
        motors.stopAll();
    }
}
