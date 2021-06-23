package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EPort {
    AUCUN(false),
    NORD(true),
    SUD(true),

    // indique une action en cours pour aller au port
    WIP_NORD(false),
    WIP_SUD(false);

    @Getter
    private final boolean inPort;
}
