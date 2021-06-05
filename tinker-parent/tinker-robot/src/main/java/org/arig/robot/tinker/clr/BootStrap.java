package org.arig.robot.tinker.clr;

import lombok.RequiredArgsConstructor;
import org.arig.robot.tinker.services.ServosServices;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootStrap implements CommandLineRunner {

    private final AbstractPropulsionsMotors motors;
    private final ServosServices servosServices;

    @Override
    public void run(final String... args) throws Exception {
        motors.init();
        motors.printVersion();
        motors.stopAll();

        servosServices.fourcheHaut();
        servosServices.blocageDroitOuvert();
        servosServices.blocageGaucheOuvert();
        servosServices.translateurCentre();
    }
}
