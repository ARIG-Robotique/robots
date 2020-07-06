package org.arig.robot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.IServosServices;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConConstants;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConEvent;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConEventListener;

@Slf4j
@RequiredArgsConstructor
public class JoyConLeftEventListener implements JoyConEventListener {

    private final IServosServices servosServices;

    @Override
    public void handleInput(final JoyConEvent event) {
        event.getNewInputs().forEach((button, enable) -> {
            if (!enable) {
                return;
            }

            if (button == JoyConConstants.l) {
                servosServices.blocageGaucheOuvert();
            }
            if (button == JoyConConstants.zl) {
                servosServices.blocageGaucheFerme();
            }
            if (button == JoyConConstants.down) {
                servosServices.fourcheHaut();
            }
            if (button == JoyConConstants.up) {
                servosServices.fourcheBas();
            }
        });

        log.info("Left Stick : Turn {}", event.getHorizontal());
    }
}
