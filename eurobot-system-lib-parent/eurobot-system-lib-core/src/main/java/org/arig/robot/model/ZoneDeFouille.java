package org.arig.robot.model;

public class ZoneDeFouille {

    private Team team;

    private CarreeFouille[] carreeFouilles = new CarreeFouille[]{
            new CarreeFouille(1),
            new CarreeFouille(2, CouleurCarreeFouille.JAUNE),
            new CarreeFouille(3),
            new CarreeFouille(4),
            new CarreeFouille(5),
            new CarreeFouille(6),
            new CarreeFouille(7),
            new CarreeFouille(8),
            new CarreeFouille(9, CouleurCarreeFouille.VIOLET),
            new CarreeFouille(10)
    };

    void refreshTeam(Team team) {
        this.team = team;
        if (team == Team.JAUNE) {
            // Les 3 extrèmes droites sont violet (pour simplifier)
            for (int i = 0 ; i < carreeFouilles.length ; i++) {
                if (i == 1) {
                    carreeFouilles[i].couleur(CouleurCarreeFouille.JAUNE);
                } else if (i >= 7 ) {
                    carreeFouilles[i].couleur(CouleurCarreeFouille.VIOLET);
                } else {
                    carreeFouilles[i].couleur(CouleurCarreeFouille.INCONNU);
                }
            }
        } else {
            // Les 3 extrèmes gauches sont jaune (pour simplifier)
            for (int i = 0 ; i < carreeFouilles.length ; i++) {
                if (i == 8) {
                    carreeFouilles[i].couleur(CouleurCarreeFouille.VIOLET);
                } else if (i <= 2 ) {
                    carreeFouilles[i].couleur(CouleurCarreeFouille.JAUNE);
                } else {
                    carreeFouilles[i].couleur(CouleurCarreeFouille.INCONNU);
                }
            }
        }
    }

    CarreeFouille get(int numero) {
        return carreeFouilles[numero - 1];
    }

    int score() {
        int points = 0;
        for (int i = 0 ; i < carreeFouilles.length ; i++) {
            // On ignore les 3 extrèmes gauches et droites en fonction de la team
            if ((i <= 2 && team == Team.VIOLET) || (i >= 7 && team == Team.JAUNE)) {
                continue;
            }

            // Contrôle carrée de fouille pas basculé, pas de points
            if (!carreeFouilles[i].bascule()) {
                continue;
            }

            // On a basculé un interdit, game over
            if (carreeFouilles[i].couleur() == CouleurCarreeFouille.INTERDIT) {
                return 0;
            }

            if ((team == Team.JAUNE && carreeFouilles[i].couleur() == CouleurCarreeFouille.JAUNE)
                || (team == Team.VIOLET && carreeFouilles[i].couleur() == CouleurCarreeFouille.VIOLET)) {
                points += 5;
            }
        }

        return points + (points > 0 ? 5 : 0);
    }
}
