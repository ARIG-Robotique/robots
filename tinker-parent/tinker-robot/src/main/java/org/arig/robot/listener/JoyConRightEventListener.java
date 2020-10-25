package org.arig.robot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.IServosServices;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerConstants;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEvent;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEventListener;

@Slf4j
@RequiredArgsConstructor
public class JoyConRightEventListener implements ControllerEventListener {

    private final IServosServices servosServices;

    @Override
    public void handleInput(final ControllerEvent event) {
        event.getNewInputs().forEach((button, enable) -> {
            if (!enable) {
                return;
            }

            if (button == ControllerConstants.r) {
                servosServices.blocageDroitOuvert();
            }
            if (button == ControllerConstants.zr) {
                servosServices.blocageDroitFerme();
            }
            if (button == ControllerConstants.x) {
                servosServices.translateurCentre();
            }
            if (button == ControllerConstants.y) {
                servosServices.transleteurGauche();
            }
            if (button == ControllerConstants.a) {
                servosServices.translateurDroite();
            }
        });

        //log.info("Right Stick : Accel {}", event.getVertical());
    }
}
