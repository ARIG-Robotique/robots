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
    void testEquipeDone5Jaune() {
        panneauxSolaire.team(Team.JAUNE);
      panneauxSolaire.equipeDone(5, 0);
        Assertions.assertEquals(panneauxSolaire.get(1).couleur(), CouleurPanneauSolaire.AUCUNE);
        Assertions.assertEquals(panneauxSolaire.get(2).couleur(), CouleurPanneauSolaire.AUCUNE);
        Assertions.assertEquals(panneauxSolaire.get(3).couleur(), CouleurPanneauSolaire.AUCUNE);
        Assertions.assertEquals(panneauxSolaire.get(4).couleur(), CouleurPanneauSolaire.AUCUNE);
        Assertions.assertEquals(panneauxSolaire.get(5).couleur(), CouleurPanneauSolaire.JAUNE);
        Assertions.assertEquals(panneauxSolaire.get(6).couleur(), CouleurPanneauSolaire.JAUNE);
        Assertions.assertEquals(panneauxSolaire.get(7).couleur(), CouleurPanneauSolaire.JAUNE);
        Assertions.assertEquals(panneauxSolaire.get(8).couleur(), CouleurPanneauSolaire.JAUNE);
        Assertions.assertEquals(panneauxSolaire.get(9).couleur(), CouleurPanneauSolaire.JAUNE);
    }

    @Test
    void testEquipeDone3Bleu() {
        panneauxSolaire.team(Team.BLEU);
      panneauxSolaire.equipeDone(3, 0);
        Assertions.assertEquals(panneauxSolaire.get(1).couleur(), CouleurPanneauSolaire.BLEU);
        Assertions.assertEquals(panneauxSolaire.get(2).couleur(), CouleurPanneauSolaire.BLEU);
        Assertions.assertEquals(panneauxSolaire.get(3).couleur(), CouleurPanneauSolaire.BLEU);
        Assertions.assertEquals(panneauxSolaire.get(4).couleur(), CouleurPanneauSolaire.AUCUNE);
        Assertions.assertEquals(panneauxSolaire.get(5).couleur(), CouleurPanneauSolaire.AUCUNE);
        Assertions.assertEquals(panneauxSolaire.get(6).couleur(), CouleurPanneauSolaire.AUCUNE);
        Assertions.assertEquals(panneauxSolaire.get(7).couleur(), CouleurPanneauSolaire.AUCUNE);
        Assertions.assertEquals(panneauxSolaire.get(8).couleur(), CouleurPanneauSolaire.AUCUNE);
        Assertions.assertEquals(panneauxSolaire.get(9).couleur(), CouleurPanneauSolaire.AUCUNE);
    }

    @Test
    void testFirst() {
        panneauxSolaire.team(Team.JAUNE);

        Assertions.assertEquals(9, panneauxSolaire.nextPanneauSolaireToProcess(10, false).numero());

        panneauxSolaire.team(Team.BLEU);

        Assertions.assertEquals(1, panneauxSolaire.nextPanneauSolaireToProcess(10, false).numero());
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
                panneauSolaire.couleur(CouleurPanneauSolaire.JAUNE);
            }
        }
        Assertions.assertEquals(15, panneauxSolaire.score());

        panneauxSolaire.get(5).couleur(CouleurPanneauSolaire.JAUNE);
        panneauxSolaire.get(6).couleur(CouleurPanneauSolaire.JAUNE);
        Assertions.assertEquals(25, panneauxSolaire.score());
    }


}
