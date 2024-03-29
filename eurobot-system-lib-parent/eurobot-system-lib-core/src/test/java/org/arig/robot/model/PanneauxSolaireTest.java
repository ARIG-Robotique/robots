package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PanneauxSolaireTest {

    private PanneauxSolaire panneauxSolaire;

    @BeforeEach
    void setUp() {
        panneauxSolaire = new PanneauxSolaire();
    }

    @Test
    void testInit() {
        Assertions.assertEquals(CouleurPanneauSolaire.AUCUNE, panneauxSolaire.get(1).couleur());
        Assertions.assertEquals(CouleurPanneauSolaire.AUCUNE, panneauxSolaire.get(2).couleur());
        Assertions.assertEquals(CouleurPanneauSolaire.AUCUNE, panneauxSolaire.get(3).couleur());
        Assertions.assertEquals(CouleurPanneauSolaire.AUCUNE, panneauxSolaire.get(4).couleur());
        Assertions.assertEquals(CouleurPanneauSolaire.AUCUNE, panneauxSolaire.get(5).couleur());
        Assertions.assertEquals(CouleurPanneauSolaire.AUCUNE, panneauxSolaire.get(6).couleur());
        Assertions.assertEquals(CouleurPanneauSolaire.AUCUNE, panneauxSolaire.get(7).couleur());
        Assertions.assertEquals(CouleurPanneauSolaire.AUCUNE, panneauxSolaire.get(8).couleur());
        Assertions.assertEquals(CouleurPanneauSolaire.AUCUNE, panneauxSolaire.get(9).couleur());

        Assertions.assertEquals(0, panneauxSolaire.score());
    }

    @Test
    void testScoreTeamBourrinTournePlusQuilNeFautLesSiens() {
        panneauxSolaire.team(Team.JAUNE);

        for (int i = 1 ; i <= 6 ; i++) {
            PanneauSolaire panneauSolaire = panneauxSolaire.get(i);
            panneauSolaire.couleur(CouleurPanneauSolaire.JAUNE_ET_BLEU);
        }

        Assertions.assertEquals(30, panneauxSolaire.score());
    }

    @Test
    void testScoreTeamBourrinTournePlusQuilNeFautLesSiensEtNousCorrectementLesNotres() {
        panneauxSolaire.team(Team.JAUNE);

        for (int i = 1 ; i <= 9 ; i++) {
            PanneauSolaire panneauSolaire = panneauxSolaire.get(i);
            if (i <= 6) {
                panneauSolaire.couleur(CouleurPanneauSolaire.JAUNE_ET_BLEU);
            } else {
                panneauSolaire.couleur(CouleurPanneauSolaire.JAUNE);
            }
        }

        Assertions.assertEquals(45, panneauxSolaire.score());
    }

    @Test
    void testScoreNormal() {
        panneauxSolaire.team(Team.JAUNE);

        for (int i = 1 ; i <= 9 ; i++) {
            PanneauSolaire panneauSolaire = panneauxSolaire.get(i);
            if (i <= 6) {
                panneauSolaire.couleur(CouleurPanneauSolaire.BLEU);
            } else {
                panneauSolaire.couleur(CouleurPanneauSolaire.TEMP_JAUNE);
            }
        }
        Assertions.assertEquals(15, panneauxSolaire.score());

        panneauxSolaire.get(5).couleur(CouleurPanneauSolaire.TEMP_JAUNE);
        panneauxSolaire.get(6).couleur(CouleurPanneauSolaire.TEMP_JAUNE);
        panneauxSolaire.refreshFromCamera(
                CouleurPanneauSolaire.BLEU, CouleurPanneauSolaire.BLEU, CouleurPanneauSolaire.BLEU,
                CouleurPanneauSolaire.BLEU, CouleurPanneauSolaire.AUCUNE, CouleurPanneauSolaire.AUCUNE,
                CouleurPanneauSolaire.JAUNE, CouleurPanneauSolaire.JAUNE, CouleurPanneauSolaire.JAUNE
        );
        Assertions.assertEquals(25, panneauxSolaire.score());
    }


}
