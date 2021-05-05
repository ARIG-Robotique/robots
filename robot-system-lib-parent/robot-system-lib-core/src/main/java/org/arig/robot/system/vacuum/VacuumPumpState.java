package org.arig.robot.system.vacuum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VacuumPumpState {
    DISABLED((byte) 0x03),
    ON((byte) 0x01),
    OFF((byte) 0x00);

    @Getter
    private final byte value;
}
