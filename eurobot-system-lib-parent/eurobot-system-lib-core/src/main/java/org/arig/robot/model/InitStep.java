package org.arig.robot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum InitStep {
    // Steps de calage des robots
    START(0),
    NERELL_CALAGE_TERMINE(1),
    NERELL_EN_POSITION(2);

    @Getter
    @Accessors(fluent = true)
    private final int step;
}
