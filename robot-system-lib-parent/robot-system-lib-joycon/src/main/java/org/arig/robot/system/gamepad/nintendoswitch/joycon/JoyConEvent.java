package org.arig.robot.system.gamepad.nintendoswitch.joycon;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@RequiredArgsConstructor
public class JoyConEvent {
    private final Map<JoyConButton, Boolean> newInputs;
    private final float horizontal;
    private final float vertical;
    private final byte battery;

    static JoyConEvent fromJoyCon(JoyCon j) {
        return new JoyConEvent(j.inputs(), j.horizontal(), j.vertical(), j.battery());
    }
}
