package org.arig.robot.system.gamepad.nintendoswitch;

import lombok.RequiredArgsConstructor;
import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

@RequiredArgsConstructor
public class ControllerInputListener implements InputReportListener {

    private float lastHorizontal = 0f;
    private float lastVertical = 0f;

    private final Controller controller;

    @Override
    public void onInputReport(final HidDevice source, final byte reportID, final byte[] reportData, final int reportLength) {
        // Input code case
        if (reportID == 0x30) {
            controller.processData(reportData);
            if (controller.eventListener() != null) {
                if (!controller.inputs().isEmpty() || (controller.horizontal() != this.lastHorizontal || controller.vertical() != this.lastVertical)) {
                    controller.eventListener().handleInput(ControllerEvent.fromController(controller));
                    this.lastHorizontal = controller.horizontal();
                    this.lastVertical = controller.vertical();
                }
            }

        // Subcommand code case (calibration data)
        } else if (reportID == 33) {
            if (reportData[12] == -112) {
                int[] factory_stick_cal = new int[18];
                for (int i = 19; i < 37; i++) {
                    int c;
                    byte b = reportData[i];
                    if (b < 0) {
                        c = b + 256;
                    } else {
                        c = b;
                    }
                    factory_stick_cal[i - 19] = c;
                }
                controller.saveCalibration(factory_stick_cal);
            }
        }
    }
}
