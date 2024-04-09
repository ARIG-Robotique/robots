package org.arig.robot.model.bras;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PositionBras {
    // commun
    INIT(true),
    HORIZONTAL(false),
    CALLAGE_PANNEAUX(false),
    TRANSPORT(false),

    // avant
    PRISE_STOCK(false),
    DEPOSE_STOCK(false),
    ;

    private final boolean inside;

}
