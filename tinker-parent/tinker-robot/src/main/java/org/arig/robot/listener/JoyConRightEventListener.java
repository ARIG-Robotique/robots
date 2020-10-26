package org.arig.robot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.IServosServices;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerConstants;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEvent;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEventListener;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;

@Slf4j
@RequiredArgsConstructor
public class JoyConRightEventListener implements ControllerEventListener {

    private final IServosServices servosServices;
    private final AbstractPropulsionsMotors motors;

    @Override
    public void handleInput(final ControllerEvent event) {
        event.getNewInputs().forEach((button, enable) -> {
            if (!enable) {
                return;
            }

            if (button == ControllerConstants.y) {
                servosServices.toggleFourche();
            }
            if (button == ControllerConstants.b) {
                servosServices.toggleBlocageGauche();
            }
            if (button == ControllerConstants.x) {
                servosServices.toggleBlocageDroit();
            }
            if (button == ControllerConstants.sl) {
                servosServices.translateurGauche();
            }
            if (button == ControllerConstants.sr) {
                servosServices.translateurDroite();
            }
        });

        float h = event.getVertical();
        float v = -event.getHorizontal();

        int g = (int) ((v + h) * 127);
        int d = (int) ((v - h) * 127);

        motors.moteurDroit(d);
        motors.moteurGauche(g);
    }
}
