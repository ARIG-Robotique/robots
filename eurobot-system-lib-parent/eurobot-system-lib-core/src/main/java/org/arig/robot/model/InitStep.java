package org.arig.robot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum InitStep {
    // Steps de calage des robots
    START(0),

    PAMI_TRIANGLE_CALAGE_TERMINE(1),
    PAMI_CARRE_CALAGE_TERMINE(2),
    PAMI_ROND_CALAGE_TERMINE(3),
    PAMI_ETOILE_CALAGE_TERMINE(4);

    @Getter
    @Accessors(fluent = true)
    private final int step;
}
