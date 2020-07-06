package org.arig.robot.scheduler;

import lombok.RequiredArgsConstructor;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConLeft;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConRight;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JoyConScheduler {

    private final JoyConLeft joyConLeft;
    private final JoyConRight joyConRight;

    @Scheduled(fixedDelay = 3000)
    public void leftConnection() {
        if (!joyConLeft.connected()) {
            joyConLeft.open();
        }
    }

    @Scheduled(fixedDelay = 3000)
    public void rightConnection() {
        if (!joyConRight.connected()) {
            joyConRight.open();
        }
    }
}
