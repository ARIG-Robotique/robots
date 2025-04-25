package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Strategy {
    QUALIF("Qualification"),
    FINALE_1("Finale 1"),
    FINALE_2("Finale 2"),;

    @Getter
    @Accessors(fluent = true)
    private final String description;
}
