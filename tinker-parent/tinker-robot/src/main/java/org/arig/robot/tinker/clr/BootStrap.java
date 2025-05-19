package org.arig.robot.tinker.clr;

import lombok.RequiredArgsConstructor;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.tinker.services.ServosServicesImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootStrap implements CommandLineRunner {

  private final AbstractPropulsionsMotors motors;
  private final ServosServicesImpl servosServicesImpl;

  @Override
  public void run(final String... args) throws Exception {
    motors.init();
    motors.printVersion();
    motors.stopAll();

    servosServicesImpl.fourcheHaut();
    servosServicesImpl.blocageDroitOuvert();
    servosServicesImpl.blocageGaucheOuvert();
    servosServicesImpl.translateurCentre();
  }
}
