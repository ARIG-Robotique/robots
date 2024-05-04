package org.arig.robot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Accessors(fluent = true, chain = true)
public class PanneauxSolaire {

    @Setter
    private Team team;

    // indique que l'action des panneaux équipe a déjà été essayée
    // et que les panneaux équipe peuvent être traités par l'action unitaire si besoin
    // initialisé à true mais mis à false dans le conctructeur de PanneauSolaireEquipeAction
    @Getter
    @Setter
    private boolean triedActionEquipe = true;

    PanneauSolaire[] data = new PanneauSolaire[]{
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
        return data[numero - 1];
    }

    public boolean isComplete() {
        return nextPanneauSolaireToProcess(Integer.MAX_VALUE) == null;
    }

    public PanneauSolaire nextPanneauSolaireToProcess(int nbTry) {
        return nextPanneauSolaireToProcess(nbTry, false);
    }

    public PanneauSolaire nextPanneauSolaireToProcess(int nbTry, boolean reverse) {
        if (team == Team.BLEU) {
            int init = reverse ? data.length - 3 : 1;
            int inc = reverse ? -1 : 1;
            for (int i = init; reverse ? i >= 1 : i <= data.length - 3; i += inc) {
                PanneauSolaire ps = get(i);
                if (ps.besoinDeTourner(team) && ps.nbTry() <= nbTry) {
                    return ps;
                }
            }
        } else {
            int init = reverse ? 4 : data.length;
            int inc = reverse ? 1 : -1;
            for (int i = init; reverse ? i <= data.length : i >= 4; i += inc) {
                PanneauSolaire ps = get(i);
                if (ps.besoinDeTourner(team) && ps.nbTry() <= nbTry) {
                    return ps;
                }
            }
        }

        return null;
    }

    public void refreshFromCamera(CouleurPanneauSolaire... couleurPanneaux) {
        if (couleurPanneaux.length != data.length) {
            throw new IllegalArgumentException("Nombre de couleur de panneaux incorrect");
        }
        for (int i = 0; i < couleurPanneaux.length; i++) {
            CouleurPanneauSolaire newColor = couleurPanneaux[i];
            CouleurPanneauSolaire oldColor = data[i].couleur();
            if (oldColor == CouleurPanneauSolaire.AUCUNE && newColor != CouleurPanneauSolaire.AUCUNE) {
                log.info("Panneau {}, changement depuis la camera {} -> {}", i + 1, oldColor.name(), newColor.name());
                data[i].couleur(newColor);
            }
        }
    }

    int score() {
        int points = 0;

        for (int i = 0; i < data.length; i++) {
            if (data[i].couleurValide(team)) {
                points += 5;
            }
        }

        return points;
    }

    public boolean equipeDone() {
        if (team == Team.BLEU) {
            for (int i = 1; i <= 3; i++) {
                if (data[i - 1].besoinDeTourner(team)) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i = 7; i <= 9; i++) {
                if (data[i - 1].besoinDeTourner(team)) {
                    return false;
                }
            }
            return true;
        }
    }

    public void equipeDone(int nb) {
        log.info("[RS] panneaux solaires équipe done : {}", nb);
        if (team == Team.BLEU) {
            for (int i = 1; i <= nb; i++) {
                data[i - 1].couleur(CouleurPanneauSolaire.BLEU);
            }
        } else {
            for (int i = 1; i <= nb; i++) {
                data[9 - i].couleur(CouleurPanneauSolaire.JAUNE);
            }
        }
    }
}
