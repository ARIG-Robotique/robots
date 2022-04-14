package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SiteDeRetour {
    AUCUN(false),

    // Retour au campement
    WIP_CAMPEMENT(false),
    CAMPEMENT(true),

    // Retour au site de fouille
    WIP_FOUILLE_CENTRE(false),
    WIP_FOUILLE_NORD(false),
    WIP_FOUILLE_SUD(false),
    WIP_FOUILLE_EST(false),
    WIP_FOUILLE_OUEST(false),
    FOUILLE_CENTRE(true),
    FOUILLE_NORD(true),
    FOUILLE_SUD(true),
    FOUILLE_EST(true),
    FOUILLE_OUEST(true);

    @Getter
    private final boolean inSite;
}
