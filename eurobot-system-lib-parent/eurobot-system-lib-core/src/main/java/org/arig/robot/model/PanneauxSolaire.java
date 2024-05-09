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

    @Setter
    private boolean preferPanneaux;

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
        return nextPanneauSolaireToProcess() == null;
    }

    public PanneauSolaire nextPanneauSolaireToProcess() {
        return nextPanneauSolaireToProcess(false);
    }

    public PanneauSolaire nextPanneauSolaireToProcess(boolean reverse) {
        if (team == Team.BLEU) {
            int init = reverse ? data.length - 3 : 1;
            int inc = reverse ? -1 : 1;
            for (int i = init; reverse ? i >= 1 : i <= data.length - 3; i += inc) {
                PanneauSolaire ps = get(i);
                if (ps.besoinDeTourner(team, preferPanneaux) && entryPanneau(ps) != null) {
                    return ps;
                }
            }
        } else {
            int init = reverse ? 4 : data.length;
            int inc = reverse ? 1 : -1;
            for (int i = init; reverse ? i <= data.length : i >= 4; i += inc) {
                PanneauSolaire ps = get(i);
                if (ps.besoinDeTourner(team, preferPanneaux) && entryPanneau(ps) != null) {
                    return ps;
                }
            }
        }

        return null;
    }

    public PanneauSolaire entryPanneau(PanneauSolaire firstPanneau) {
        if (!firstPanneau.blocked()) {
            return firstPanneau;
        }

        if (firstPanneau.numero() == 1 || firstPanneau.numero() == 4 || firstPanneau.numero() == 7) {
            for (int i = firstPanneau.numero(); i <= firstPanneau.numero() + 2; i++) {
                if (get(i).blocked()) continue;
                return get(i);
            }
        } else if (firstPanneau.numero() == 3 || firstPanneau.numero() == 6 || firstPanneau.numero() == 9) {
            for (int i = firstPanneau.numero(); i >= firstPanneau.numero() - 2; i--) {
                if (get(i).blocked()) continue;
                return get(i);
            }
        } else if (!get(firstPanneau.numero() - 1).blocked()) {
            return get(firstPanneau.numero() - 1);
        } else if (!get(firstPanneau.numero() + 1).blocked()) {
            return get(firstPanneau.numero() + 1);
        }

        return null;
    }

    public void refreshFromCamera(int nb, CouleurPanneauSolaire couleur, long millis, int r) {
        if (data[nb - 1].millis() >= millis) {
            return;
        }

        data[nb - 1].couleur(couleur).millis(millis).rotation(r);
        log.info("[rs] panneau solaire changed: {}", data[nb - 1].toString());
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
                if (data[i - 1].besoinDeTourner(team, false)) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i = 7; i <= 9; i++) {
                if (data[i - 1].besoinDeTourner(team, false)) {
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
                data[i - 1].couleur(CouleurPanneauSolaire.BLEU).millis(millis).rotation(null);
            }
        } else {
            for (int i = 1; i <= nb; i++) {
                data[9 - i].couleur(CouleurPanneauSolaire.JAUNE).millis(millis).rotation(null);
            }
        }
    }
}
