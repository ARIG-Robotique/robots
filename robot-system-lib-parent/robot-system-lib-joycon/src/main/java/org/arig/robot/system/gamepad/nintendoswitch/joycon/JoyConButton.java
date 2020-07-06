package org.arig.robot.system.gamepad.nintendoswitch.joycon;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@RequiredArgsConstructor
public class JoyConButton {
    private final String name;
    private final int adress;

    public int on() {
        return adress;
    }

    public int off() {
        return -on();
    }
}
