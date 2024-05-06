package org.arig.robot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class AireDepose extends ZoneDepose {

    // aire: dépose coté bordure
    private boolean rang1 = false;
    // aire: dépose plus loin de la bordure
    private boolean rang2 = false;

    public AireDepose(String name) {
        super(name);
    }

    public SiteDeCharge siteDeCharge(Team team) {
        switch (name()) {
            case "Aire nord": return team == Team.BLEU ? SiteDeCharge.BLEU_NORD : SiteDeCharge.JAUNE_NORD;
            case "Aire milieu": return team == Team.BLEU ? SiteDeCharge.BLEU_MILIEU : SiteDeCharge.JAUNE_MILIEU;
            case "Aire sud": return team == Team.BLEU ? SiteDeCharge.BLEU_SUD : SiteDeCharge.JAUNE_SUD;
            default: return null;
        }
    }

    public int score() {
        return score(false);
    }

    public AireDepose clone() {
        AireDepose newJardiniere = new AireDepose(null);
        newJardiniere.add(data().toArray(new Plante[0]));
        return newJardiniere;
    }

}
