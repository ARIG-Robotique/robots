package org.arig.robot.system.gamepad.nintendoswitch;

@FunctionalInterface
public interface ControllerEventListener {
    void handleInput(ControllerEvent event);
}
