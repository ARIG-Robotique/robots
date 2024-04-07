package org.arig.robot.model;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PanneauxSolaire {

    @Setter
    @Accessors(fluent = true, chain = true)
    private Team team;

    PanneauSolaire[] panneauxSolaire = new PanneauSolaire[]{
            // Bleu
            new PanneauSolaire(1),
            new PanneauSolaire(2),
            new PanneauSolaire(3),

            // Common
            new PanneauSolaire(4),
            new PanneauSolaire(5),
            new PanneauSolaire(6),

            // Jaune
            new PanneauSolaire(7),
            new PanneauSolaire(8),
            new PanneauSolaire(9)
    };

    public PanneauSolaire get(int numero) {
        return panneauxSolaire[numero - 1];
    }

    boolean isComplete() {
        return nextPanneauSolaireToProcess(Integer.MAX_VALUE) == null;
    }

    PanneauSolaire nextPanneauSolaireToProcess(int nbTry) {
        return nextPanneauSolaireToProcess(nbTry, false);
    }

    PanneauSolaire nextPanneauSolaireToProcess(int nbTry, boolean reverse) {
        if (team == Team.BLEU) {
            int init = reverse ? panneauxSolaire.length - 3 : 1;
            int inc = reverse ? -1 : 1;
            for (int i = init; reverse ? i >= 1 : i <= panneauxSolaire.length - 3; i += inc) {
                PanneauSolaire ps = get(i);
                if (ps.besoinDeTourner(team) && ps.nbTry() <= nbTry) {
                    return ps;
                }
            }
        } else {
            int init = reverse ? 4 : panneauxSolaire.length;
            int inc = reverse ? 1 : -1;
            for (int i = init; reverse ? i <= panneauxSolaire.length : i >= 4; i += inc) {
                PanneauSolaire ps = get(i);
                if (ps.besoinDeTourner(team) && ps.nbTry() <= nbTry) {
                    return ps;
                }
            }
        }

        return null;
    }

    public void refreshFromCamera(CouleurPanneauSolaire ... couleurPanneaux) {
        if (couleurPanneaux.length != panneauxSolaire.length) {
            throw new IllegalArgumentException("Nombre de couleur de panneaux incorrect");
        }
        for (int i = 0 ; i < couleurPanneaux.length ; i++) {
            CouleurPanneauSolaire newColor = couleurPanneaux[i];
            CouleurPanneauSolaire oldColor = panneauxSolaire[i].couleur();
            if (oldColor == CouleurPanneauSolaire.AUCUNE && newColor != CouleurPanneauSolaire.AUCUNE) {
                log.info("Panneau {}, changement depuis la camera {} -> {}", i + 1, oldColor.name(), newColor.name());
                panneauxSolaire[i].couleur(newColor);
            }
        }
    }

    int score() {
        int points = 0;

        for (int i = 0; i < panneauxSolaire.length; i++) {
            if (panneauxSolaire[i].couleurValide(team)) {
                points += 5;
            }
        }

        return points;
    }

    public boolean equipeDone() {
        if (team == Team.BLEU) {
            for (int i = 1; i <= 3; i++) {
                if (panneauxSolaire[i-1].besoinDeTourner(team)) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i = 7; i <= 9; i++) {
                if (panneauxSolaire[i-1].besoinDeTourner(team)) {
                    return false;
                }
            }
            return true;
        }
    }

    public void equipeDone(int nb) {
        if (team == Team.BLEU) {
            for (int i = 1; i <= nb; i++) {
                panneauxSolaire[i-1].couleur(CouleurPanneauSolaire.BLEU);
            }
        } else {
            for (int i = 7; i < 7 + nb; i++) {
                panneauxSolaire[i-1].couleur(CouleurPanneauSolaire.JAUNE);
            }
        }
    }
}
