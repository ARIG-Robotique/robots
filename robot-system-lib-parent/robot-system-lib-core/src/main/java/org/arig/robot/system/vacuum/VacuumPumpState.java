package org.arig.robot.system.vacuum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VacuumPumpState {
    OFF((byte) 0x00),
    ON((byte) 0x01),
    ON_FORCE((byte) 0x02),
    DISABLED((byte) 0x03);

    @Getter
    private final byte value;
}
