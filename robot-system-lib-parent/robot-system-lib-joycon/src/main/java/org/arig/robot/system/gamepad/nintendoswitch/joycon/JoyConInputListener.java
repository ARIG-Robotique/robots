package org.arig.robot.system.gamepad.nintendoswitch.joycon;

import lombok.RequiredArgsConstructor;
import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

@RequiredArgsConstructor
public class JoyConInputListener implements InputReportListener {

    private float lastHorizontal = 0f;
    private float lastVertical = 0f;

    private final JoyCon joyCon;

    @Override
    public void onInputReport(final HidDevice source, final byte reportID, final byte[] reportData, final int reportLength) {
        // Input code case
        if (reportID == 0x30) {
            joyCon.processData(reportData);
            if (joyCon.eventListener() != null) {
                if (!joyCon.inputs().isEmpty() || (joyCon.horizontal() != this.lastHorizontal || joyCon.vertical() != this.lastVertical)) {
                    joyCon.eventListener().handleInput(JoyConEvent.fromJoyCon(joyCon));
                    this.lastHorizontal = joyCon.horizontal();
                    this.lastVertical = joyCon.vertical();
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
                joyCon.saveCalibration(factory_stick_cal);
            }
        }
    }
}
