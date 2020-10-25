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
public class JoyConLeftEventListener implements ControllerEventListener {

    private final IServosServices servosServices;
    private final AbstractPropulsionsMotors motors;

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

        float h = event.getHorizontal();
        float v = event.getVertical();

        int g = (int) ((v + h) * 127);
        int d = (int) ((v - h) * 127);

        log.info("Gauche {} ; Droite {}", g, d);
        motors.moteurDroit(d);
        motors.moteurGauche(g);
    }
}
