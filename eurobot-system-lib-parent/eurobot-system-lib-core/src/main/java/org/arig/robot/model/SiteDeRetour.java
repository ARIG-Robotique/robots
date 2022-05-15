package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SiteDeRetour {
    AUCUN(false, false, false),

    // Retour au campement
    WIP_CAMPEMENT(false, true, false),
    CAMPEMENT(true, true, false),

    // Retour au site de fouille
    WIP_FOUILLE_CENTRE(false, false, true),
    WIP_FOUILLE_NORD(false, false, true),
    WIP_FOUILLE_SUD(false, false, true),
    WIP_FOUILLE_EST(false, false, true),
    WIP_FOUILLE_OUEST(false, false, true),
    FOUILLE_CENTRE(true, false, true),
    FOUILLE_NORD(true, false, true),
    FOUILLE_SUD(true, false, true),
    FOUILLE_EST(true, false, true),
    FOUILLE_OUEST(true, false, true);

    @Getter
    private final boolean inSite;

    @Getter
    private final boolean campement;

    @Getter
    private final boolean fouille;
}
