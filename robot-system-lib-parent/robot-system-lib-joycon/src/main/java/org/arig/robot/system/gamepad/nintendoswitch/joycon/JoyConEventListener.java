package org.arig.robot.system.gamepad.nintendoswitch.joycon;

@FunctionalInterface
public interface JoyConEventListener {
    void handleInput(JoyConEvent event);
}
