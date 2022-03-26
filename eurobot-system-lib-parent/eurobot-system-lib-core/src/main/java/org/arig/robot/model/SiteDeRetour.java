package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SiteDeRetour {
    AUCUN(false),
    WIP_CAMPEMENT(false),
    CAMPEMENT(true),
    WIP_FOUILLE(false),
    FOUILLE(true);

    @Getter
    private final boolean inSite;
}
