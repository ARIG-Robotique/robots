package org.arig.robot.system.gamepad.nintendoswitch.joycon;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.gamepad.nintendoswitch.Controller;

@Slf4j
public abstract class JoyCon extends Controller {

    public JoyCon(final short controllerId, final String name) {
        super(controllerId, name);
    }

}
