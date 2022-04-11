package org.arig.robot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum InitStep {
    // Steps de calage des robots
    START(0),
    ODIN_DEVANT_GALERIE(1),
    NERELL_CALAGE_TERMINE(2),
    ODIN_EN_POSITION_BASIC(3),
    NERELL_EN_POSITION_BASIC(4);

    @Getter
    @Accessors(fluent = true)
    private final int step;
}
