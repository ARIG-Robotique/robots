package org.arig.robot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.IServosServices;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConConstants;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConEvent;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConEventListener;

@Slf4j
@RequiredArgsConstructor
public class JoyConRightEventListener implements JoyConEventListener {

    private final IServosServices servosServices;

    @Override
    public void handleInput(final JoyConEvent event) {
        event.getNewInputs().forEach((button, enable) -> {
            if (!enable) {
                return;
            }

            if (button == JoyConConstants.r) {
                servosServices.blocageDroitOuvert();
            }
            if (button == JoyConConstants.zr) {
                servosServices.blocageDroitFerme();
            }
            if (button == JoyConConstants.x) {
                servosServices.translateurCentre();
            }
            if (button == JoyConConstants.y) {
                servosServices.transleteurGauche();
            }
            if (button == JoyConConstants.a) {
                servosServices.translateurDroite();
            }
        });

        log.info("Right Stick : Accel {}", event.getVertical());
    }
}
