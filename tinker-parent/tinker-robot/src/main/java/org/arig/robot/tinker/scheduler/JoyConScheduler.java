package org.arig.robot.tinker.scheduler;

import lombok.RequiredArgsConstructor;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConLeft;
import org.arig.robot.system.gamepad.nintendoswitch.joycon.JoyConRight;
import org.arig.robot.system.gamepad.nintendoswitch.pro.ProController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JoyConScheduler {

    private final JoyConLeft joyConLeft;
    private final JoyConRight joyConRight;
    private final ProController pro;

    @Scheduled(fixedDelay = 3000)
    public void leftConnection() {
        if (!joyConLeft.connected() && !joyConRight.connected() && !pro.connected()) {
            joyConLeft.open();
        }
    }

    @Scheduled(fixedDelay = 3000)
    public void rightConnection() {
        if (!joyConRight.connected() && !joyConLeft.connected() && !pro.connected()) {
            joyConRight.open();
        }
    }

    @Scheduled(fixedDelay = 3000)
    public void proConnection() {
        if (!pro.connected() && !joyConLeft.connected() && !joyConRight.connected()) {
            pro.open();
        }
    }
}
