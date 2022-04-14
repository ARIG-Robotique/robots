package org.arig.robot.model;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.stream.Stream;

public class ZoneDeFouille {

    @Setter
    @Accessors(fluent = true, chain = true)
    private Team team;

    CarreFouille[] carresFouille = new CarreFouille[]{
            new CarreFouille(1),
            new CarreFouille(2, CouleurCarreFouille.JAUNE),
            new CarreFouille(3),
            new CarreFouille(4),
            new CarreFouille(5),
            new CarreFouille(6),
            new CarreFouille(7),
            new CarreFouille(8),
            new CarreFouille(9, CouleurCarreFouille.VIOLET),
            new CarreFouille(10)
    };

    public CarreFouille get(int numero) {
        return carresFouille[numero - 1];
    }

    boolean hasInconnu() {
        return Stream.of(carresFouille).anyMatch(cf -> cf.couleur() == CouleurCarreFouille.INCONNU);
    }

    boolean isComplete() {
        return nextCarreFouilleToProcess(Integer.MAX_VALUE) == null;
    }

    CarreFouille nextCarreFouilleToProcess(int nbTry) {
        return nextCarreFouilleToProcess(nbTry, false);
    }

    CarreFouille nextCarreFouilleToProcess(int nbTry, boolean reverse) {
        if (team == Team.JAUNE) {
            int init = reverse ? carresFouille.length - 3 : 1;
            int inc = reverse ? -1 : 1;
            for (int i = init; reverse ? i >= 1 : i <= carresFouille.length - 3; i += inc) {
                CarreFouille cf = get(i);
                if (!cf.bascule() && cf.nbTry() <= nbTry &&
                        (cf.couleur() == CouleurCarreFouille.INCONNU || cf.couleur() == CouleurCarreFouille.JAUNE)) {
                    return cf;
                }
            }
        } else if (team == Team.VIOLET) {
            int init = reverse ? 4 : carresFouille.length;
            int inc = reverse ? 1 : -1;
            for(int i = init ; reverse ? i <= carresFouille.length : i >= 4; i += inc) {
                CarreFouille cf = get(i);
                if(!cf.bascule() && cf.nbTry() <= nbTry &&
                        (cf.couleur() == CouleurCarreFouille.INCONNU || cf.couleur() == CouleurCarreFouille.VIOLET)) {
                    return cf;
                }
            }
        }

        return null;
    }

    void refreshProcessing() {
        if (!hasInconnu()) {
            return;
        }

        // Si le 1 est jaune ou le 10 violet, le 3 et le 8 sont interdit et réciroquement
        if (get(1).couleur() == CouleurCarreFouille.JAUNE || get(3).couleur() == CouleurCarreFouille.INTERDIT
                || get(8).couleur() == CouleurCarreFouille.INTERDIT || get(10).couleur() == CouleurCarreFouille.VIOLET) {
            // Coté jaune
            get(1).couleur(CouleurCarreFouille.JAUNE);
            get(3).couleur(CouleurCarreFouille.INTERDIT);

            // Coté violet
            get(8).couleur(CouleurCarreFouille.INTERDIT);
            get(10).couleur(CouleurCarreFouille.VIOLET);
        }

        // Si le 1 ou le 10 sont interdit, le 3 est jaune et le 8 est violet et réciproquement.
        if (get(1).couleur() == CouleurCarreFouille.INTERDIT || get(3).couleur() == CouleurCarreFouille.JAUNE
                || get(8).couleur() == CouleurCarreFouille.VIOLET || get(10).couleur() == CouleurCarreFouille.INTERDIT) {
            // Coté jaune
            get(1).couleur(CouleurCarreFouille.INTERDIT);
            get(3).couleur(CouleurCarreFouille.JAUNE);

            // Coté violet
            get(8).couleur(CouleurCarreFouille.VIOLET);
            get(10).couleur(CouleurCarreFouille.INTERDIT);
        }

        // Si le 4 ou le 7 sont violet, le 5 et le 6 sont jaune et reciproquement.
        if (get(4).couleur() == CouleurCarreFouille.VIOLET || get(5).couleur() == CouleurCarreFouille.JAUNE
                || get(6).couleur() == CouleurCarreFouille.JAUNE || get(7).couleur() == CouleurCarreFouille.VIOLET) {
            get(4).couleur(CouleurCarreFouille.VIOLET);
            get(5).couleur(CouleurCarreFouille.JAUNE);
            get(6).couleur(CouleurCarreFouille.JAUNE);
            get(7).couleur(CouleurCarreFouille.VIOLET);
        }

        // Si le 4 ou le 7 sont jaune, le 5 et 6 sont violet et réciproquement.
        if (get(4).couleur() == CouleurCarreFouille.JAUNE || get(5).couleur() == CouleurCarreFouille.VIOLET
                || get(6).couleur() == CouleurCarreFouille.VIOLET || get(7).couleur() == CouleurCarreFouille.JAUNE) {
            get(4).couleur(CouleurCarreFouille.JAUNE);
            get(5).couleur(CouleurCarreFouille.VIOLET);
            get(6).couleur(CouleurCarreFouille.VIOLET);
            get(7).couleur(CouleurCarreFouille.JAUNE);
        }
    }

    int score() {
        int points = 0;

        for (int i = 0; i < carresFouille.length; i++) {
            // On ignore les 3 extrèmes gauches et droites en fonction de la team
            if ((i <= 2 && team == Team.VIOLET) || (i >= 7 && team == Team.JAUNE)) {
                continue;
            }

            // Contrôle carrée de fouille pas basculé, pas de points
            if (!carresFouille[i].bascule()) {
                continue;
            }

            // On a basculé un interdit, game over
            if (carresFouille[i].couleur() == CouleurCarreFouille.INTERDIT) {
                return 0;
            }

            if ((team == Team.JAUNE && carresFouille[i].couleur() == CouleurCarreFouille.JAUNE)
                    || (team == Team.VIOLET && carresFouille[i].couleur() == CouleurCarreFouille.VIOLET)) {
                points += 5;
            }
        }

        // Si l'adversaire est bourrin, et que les 4 du milieu sont basculé en inconnu, on ajoute 10 points
        if (hasInconnu()) {
            int nbInconnuCentre = 0;
            for (int i = 3 ; i < 7 ; i++) {
                CarreFouille cf = carresFouille[i];
                if (cf.couleur() == CouleurCarreFouille.INCONNU && cf.bascule()) {
                    nbInconnuCentre++;
                }
            }

            if (nbInconnuCentre == 4) {
                // Il y en as deux des nôtres qui ont été donné par l'adversaire
                points += 10;
            }
        }

        return points + (points > 0 ? 5 : 0);
    }
}
