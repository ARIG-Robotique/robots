package org.arig.robot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.IServosServices;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerConstants;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEvent;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEventListener;

@Slf4j
@RequiredArgsConstructor
public class JoyConLeftEventListener implements ControllerEventListener {

    private final IServosServices servosServices;

    @Override
    public void handleInput(final ControllerEvent event) {
        event.getNewInputs().forEach((button, enable) -> {
            if (!enable) {
                return;
            }

            if (button == ControllerConstants.l) {
                servosServices.blocageGaucheOuvert();
            }
            if (button == ControllerConstants.zl) {
                servosServices.blocageGaucheFerme();
            }
            if (button == ControllerConstants.down) {
                servosServices.fourcheHaut();
            }
            if (button == ControllerConstants.up) {
                servosServices.fourcheBas();
            }
        });

        log.info("Left Stick : Turn {}", event.getHorizontal());
    }
}
