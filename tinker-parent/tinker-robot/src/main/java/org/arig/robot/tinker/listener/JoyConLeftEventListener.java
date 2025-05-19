package org.arig.robot.tinker.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerConstants;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEvent;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEventListener;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.tinker.services.ServosServices;

@Slf4j
@RequiredArgsConstructor
public class JoyConLeftEventListener implements ControllerEventListener {

  private final ServosServices servosServices;
  private final AbstractPropulsionsMotors motors;

  @Override
  public void handleInput(final ControllerEvent event) {
    event.getNewInputs().forEach((button, enable) -> {
      if (!enable) {
        return;
      }

      if (button == ControllerConstants.right) {
        servosServices.toggleFourche();
      }
      if (button == ControllerConstants.up) {
        servosServices.toggleBlocageGauche();
      }
      if (button == ControllerConstants.down) {
        servosServices.toggleBlocageDroit();
      }
      if (button == ControllerConstants.sl) {
        servosServices.translateurGauche();
      }
      if (button == ControllerConstants.sr) {
        servosServices.translateurDroite();
      }
    });

    float h = -event.getVertical();
    float v = event.getHorizontal();

    int g = (int) ((v + h) * 127);
    int d = (int) ((v - h) * 127);

    motors.moteurDroit(d);
    motors.moteurGauche(g);
  }
}
