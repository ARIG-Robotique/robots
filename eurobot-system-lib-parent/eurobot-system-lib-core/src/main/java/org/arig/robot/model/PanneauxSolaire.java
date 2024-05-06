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
    // initialisé à true mais mis à false dans le constructeur de PanneauSolaireEquipeAction
    @Getter
    @Setter
    private boolean triedActionEquipe = true;

    PanneauSolaire[] data = new PanneauSolaire[]{
            // Bleu
        new PanneauSolaire(1).millis(Long.MIN_VALUE),
        new PanneauSolaire(2).millis(Long.MIN_VALUE),
        new PanneauSolaire(3).millis(Long.MIN_VALUE),

            // Common
        new PanneauSolaire(4).millis(Long.MIN_VALUE),
        new PanneauSolaire(5).millis(Long.MIN_VALUE),
        new PanneauSolaire(6).millis(Long.MIN_VALUE),

            // Jaune
        new PanneauSolaire(7).millis(Long.MIN_VALUE),
        new PanneauSolaire(8).millis(Long.MIN_VALUE),
        new PanneauSolaire(9).millis(Long.MIN_VALUE)
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

  public void refreshFromCamera(int nb, CouleurPanneauSolaire couleur, long millis) {
    if (data[nb - 1].millis() >= millis) {
      return;
    }

    data[nb - 1].couleur(couleur).millis(millis);
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

  public void equipeDone(int nb, long millis) {
        log.info("[RS] panneaux solaires équipe done : {}", nb);
        if (team == Team.BLEU) {
            for (int i = 1; i <= nb; i++) {
              data[i - 1].couleur(CouleurPanneauSolaire.BLEU).millis(millis);
            }
        } else {
            for (int i = 1; i <= nb; i++) {
              data[9 - i].couleur(CouleurPanneauSolaire.JAUNE).millis(millis);
            }
        }
    }
}
