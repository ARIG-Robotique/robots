package org.arig.robot.utils;

import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.communication.balise.enums.CouleurDetectee;

public enum EcueilUtils {
    ;

    public static CouleurDetectee[] couleurDetectees(ECouleurBouee[] bouees) {
        CouleurDetectee[] r = new CouleurDetectee[bouees.length];
        for (int i = 0 ; i < bouees.length ; i++) {
            ECouleurBouee b = bouees[i];
            if (b == ECouleurBouee.ROUGE) {
                r[i] = CouleurDetectee.RED;
            } else if (b == ECouleurBouee.VERT) {
                r[i] = CouleurDetectee.GREEN;
            } else {
                r[i] = CouleurDetectee.UNKNOWN;
            }
        }
        return r;
    }

    public static ECouleurBouee[] tirageEquipe(ETeam team) {
        if (team == ETeam.JAUNE) {
            return new ECouleurBouee[]{ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT};
        } else if (team == ETeam.BLEU) {
            return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE};
        } else {
            return new ECouleurBouee[]{ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU};
        }
    }

    public static ECouleurBouee[] tirageCommunAdverse(ETeam team, int tirage) {
        if (team == ETeam.JAUNE) {
            switch(tirage) {
                case 1:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT};
                case 2:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.VERT};
                case 3:
                default:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.VERT, ECouleurBouee.VERT};
            }
        } else if (team == ETeam.BLEU) {
            switch(tirage) {
                case 1:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.VERT};
                case 2:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT};
                case 3:
                default:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.VERT};
            }
        } else {
            return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};
        }
    }

    public static ECouleurBouee[] tirageCommunEquipe(ETeam team, int tirage) {
        if (team == ETeam.JAUNE) {
            switch(tirage) {
                case 1:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.VERT};
                case 2:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT};
                case 3:
                default:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.VERT};
            }
        } else if (team == ETeam.BLEU) {
            switch(tirage) {
                case 1:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT};
                case 2:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.VERT};
                case 3:
                default:
                    return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.VERT, ECouleurBouee.VERT};
            }
        } else {
            return new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};
        }
    }
}
