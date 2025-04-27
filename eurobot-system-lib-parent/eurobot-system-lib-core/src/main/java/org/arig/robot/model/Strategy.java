package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static Strategy fromDescription(String description) {
        for (Strategy strategy : values()) {
            if (strategy.description.equalsIgnoreCase(description)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Unknown strategy: " + description);
    }
}
