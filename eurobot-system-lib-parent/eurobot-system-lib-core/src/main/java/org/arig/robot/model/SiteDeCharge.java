package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SiteDeCharge {
    AUCUN(false),

    // Blue charging station
    WIP_BLEU_NORD(false),
    WIP_BLEU_MILIEU(false),
    WIP_BLEU_SUD(false),
    BLEU_NORD(true),
    BLEU_MILIEU(true),
    BLEU_SUD(true),

    // Yellow charging station
    WIP_JAUNE_NORD(false),
    WIP_JAUNE_MILIEU(false),
    WIP_JAUNE_SUD(false),
    JAUNE_NORD(true),
    JAUNE_MILIEU(true),
    JAUNE_SUD(true);

    @Getter
    private final boolean enCharge;
}
