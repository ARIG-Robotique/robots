package org.arig.robot.model.bras;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PositionBras {
    // commun
    INIT(true),
    HORIZONTAL(true),
    CALLAGE_PANNEAUX(false),

    PRISE_PLANTE_AVANT(false), // dans le bloque plantes avant
    PRISE_POT(false),
    PRISE_POT_POT(false), // un pot dans un pot
    SORTIE_POT_POT(false), // pour sortir un pot d'un autre pot
    DEPOSE_PLANTE_POT(false), // pour poser une plante dans un pot

    DEPOSE_SOL(false),
    DEPOSE_JARDINIERE(false),

    // avant
    PRISE_STOCK(false),
    DEPOSE_STOCK(false),
    ;

    private final boolean inside;

}
