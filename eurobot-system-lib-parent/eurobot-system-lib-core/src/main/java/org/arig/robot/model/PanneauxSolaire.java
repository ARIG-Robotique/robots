package org.arig.robot.model;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.balise.enums.ZoneMines;

import java.util.List;

@Slf4j
@Accessors(fluent = true, chain = true)
public class PanneauxSolaire {

    @Setter
    private Team team;

    @Setter
    private boolean preferPanneaux;

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

    public boolean isComplete(List<ZoneMines> mines) {
        return nextPanneauSolaireToProcess(mines) == null;
    }

    public PanneauSolaire nextPanneauSolaireToProcess(List<ZoneMines> mines) {
        return nextPanneauSolaireToProcess(false, mines);
    }

    public PanneauSolaire nextPanneauSolaireToProcess(boolean reverse, List<ZoneMines> mines) {
        if (team == Team.BLEU) {
            int init = reverse ? data.length - 3 : 1;
            int inc = reverse ? -1 : 1;
            for (int i = init; reverse ? i >= 1 : i <= data.length - 3; i += inc) {
                PanneauSolaire ps = get(i);
                if (ps.besoinDeTourner(team, preferPanneaux) && entryPanneau(ps, mines) != null) {
                    return ps;
                }
            }
        } else {
            int init = reverse ? 4 : data.length;
            int inc = reverse ? 1 : -1;
            for (int i = init; reverse ? i <= data.length : i >= 4; i += inc) {
                PanneauSolaire ps = get(i);
                if (ps.besoinDeTourner(team, preferPanneaux) && entryPanneau(ps, mines) != null) {
                    return ps;
                }
            }
        }

        return null;
    }

    public PanneauSolaire entryPanneau(PanneauSolaire firstPanneau, List<ZoneMines> mines) {
        if (firstPanneau == null) {
            return null;
        }

        if (!firstPanneau.blocked() && !mines.contains(ZoneMines.getPanneau(firstPanneau.numero()))) {
            return firstPanneau;
        }

        if (firstPanneau.numero() == 1 || firstPanneau.numero() == 4 || firstPanneau.numero() == 7) {
            for (int i = firstPanneau.numero(); i <= firstPanneau.numero() + 2; i++) {
                if (get(i).blocked() || mines.contains(ZoneMines.getPanneau(i))) continue;
                return get(i);
            }
        } else if (firstPanneau.numero() == 3 || firstPanneau.numero() == 6 || firstPanneau.numero() == 9) {
            for (int i = firstPanneau.numero(); i >= firstPanneau.numero() - 2; i--) {
                if (get(i).blocked() || mines.contains(ZoneMines.getPanneau(i))) continue;
                return get(i);
            }
        } else if (!get(firstPanneau.numero() - 1).blocked() && !mines.contains(ZoneMines.getPanneau(firstPanneau.numero() - 1))) {
            return get(firstPanneau.numero() - 1);
        } else if (!get(firstPanneau.numero() + 1).blocked() && !mines.contains(ZoneMines.getPanneau(firstPanneau.numero() + 1))) {
            return get(firstPanneau.numero() + 1);
        }

        if (firstPanneau.numero() >= 4 && firstPanneau.numero() <= 6
            && (get(4).blocked() || mines.contains(ZoneMines.getPanneau(4)))
            && (get(5).blocked() || mines.contains(ZoneMines.getPanneau(5)))
            && (get(6).blocked() || mines.contains(ZoneMines.getPanneau(6)))) {
            if (team == Team.BLEU) {
                return (get(3).blocked() || mines.contains(ZoneMines.getPanneau(3))) ? null : get(3);
            } else {
                return (get(7).blocked() || mines.contains(ZoneMines.getPanneau(7))) ? null : get(7);
            }
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

    public boolean communModifiedByOpponent() {
        for (int i = 3; i <= 5; i++) {
            if (data[i].couleur() == CouleurPanneauSolaire.JAUNE_ET_BLEU
                    || data[i].couleur() == (team == Team.BLEU ? CouleurPanneauSolaire.JAUNE : CouleurPanneauSolaire.BLEU)) {
                return true;
            }
        }
        return false;
    }
}
