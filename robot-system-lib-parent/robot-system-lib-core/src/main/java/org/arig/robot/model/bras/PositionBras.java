package org.arig.robot.model.bras;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PositionBras {
    // commun
    INIT(true),
    HORIZONTAL(true),
    CALLAGE_BORDURE(false),
    PRISE_SOL_1(false),
    PRISE_SOL_2(false),
    DEPOSE_SOL(false),
    DEPOSE_JARDINIERE(false),

    // avant
    PRISE_STOCK(false),
    DEPOSE_STOCK(false),
    ;

    private final boolean inside;

}
