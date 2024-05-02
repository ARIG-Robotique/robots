package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SiteDeCharge {
    AUCUN(false, 0),

    // Blue charging station
    WIP_BLEU_NORD(false, 135),
    WIP_BLEU_MILIEU(false, 0),
    WIP_BLEU_SUD(false, -135),
    BLEU_NORD(true, 135),
    BLEU_MILIEU(true, 0),
    BLEU_SUD(true, -135),

    // Yellow charging station
    WIP_JAUNE_NORD(false, 45),
    WIP_JAUNE_MILIEU(false, 180),
    WIP_JAUNE_SUD(false, -45),
    JAUNE_NORD(true, 45),
    JAUNE_MILIEU(true, 180),
    JAUNE_SUD(true, -45);

    @Getter
    private final boolean enCharge;

    @Getter
    private final int angleDeposeAvant;
}
