package org.arig.robot.model;

import org.arig.robot.model.balise.enums.ZoneMines;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

        Assertions.assertEquals(9, panneauxSolaire.nextPanneauSolaireToProcess(false, new ArrayList<>()).numero());

        panneauxSolaire.team(Team.BLEU);

        Assertions.assertEquals(1, panneauxSolaire.nextPanneauSolaireToProcess(false, new ArrayList<>()).numero());
    }

    @Test
    void testScoreTeamBourrinTournePlusQuilNeFautLesSiens() {
        panneauxSolaire.team(Team.JAUNE);

        for (int i = 1; i <= 6; i++) {
            PanneauSolaire panneauSolaire = panneauxSolaire.get(i);
            panneauSolaire.couleur(CouleurPanneauSolaire.JAUNE_ET_BLEU);
        }

        Assertions.assertEquals(30, panneauxSolaire.score());
    }

    @Test
    void testScoreTeamBourrinTournePlusQuilNeFautLesSiensEtNousCorrectementLesNotres() {
        panneauxSolaire.team(Team.JAUNE);

        for (int i = 1; i <= 9; i++) {
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

        for (int i = 1; i <= 9; i++) {
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

    @Test
    void entryPointMines() {
        panneauxSolaire.team(Team.JAUNE);
        panneauxSolaire.equipeDone(3, 0);
        List<ZoneMines> mines = new ArrayList<>();
        mines.add(ZoneMines.SOLAR_PANEL_6);
        PanneauSolaire firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(mines);
        Assertions.assertEquals(5, panneauxSolaire.entryPanneau(firstPanneau, mines).numero());
        mines.add(ZoneMines.SOLAR_PANEL_5);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(mines);
        Assertions.assertEquals(4, panneauxSolaire.entryPanneau(firstPanneau, mines).numero());
        mines.add(ZoneMines.SOLAR_PANEL_4);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(mines);
        Assertions.assertEquals(7, panneauxSolaire.entryPanneau(firstPanneau, mines).numero());
        mines.add(ZoneMines.SOLAR_PANEL_7);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(mines);
        Assertions.assertNull(panneauxSolaire.entryPanneau(firstPanneau, mines));
    }

    @Test
    void entryPointMinesReverse() {
        panneauxSolaire.team(Team.JAUNE);
        panneauxSolaire.equipeDone(3, 0);
        List<ZoneMines> mines = new ArrayList<>();
        mines.add(ZoneMines.SOLAR_PANEL_4);
        PanneauSolaire firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(true, mines);
        Assertions.assertEquals(5, panneauxSolaire.entryPanneau(firstPanneau, mines).numero());
        mines.add(ZoneMines.SOLAR_PANEL_5);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(true, mines);
        Assertions.assertEquals(6, panneauxSolaire.entryPanneau(firstPanneau, mines).numero());
        mines.add(ZoneMines.SOLAR_PANEL_6);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(true, mines);
        Assertions.assertEquals(7, panneauxSolaire.entryPanneau(firstPanneau, mines).numero());
        mines.add(ZoneMines.SOLAR_PANEL_7);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(mines);
        Assertions.assertNull(panneauxSolaire.entryPanneau(firstPanneau, mines));
    }

    @Test
    void entryPointMinesMillieu() {
        panneauxSolaire.team(Team.BLEU);
        panneauxSolaire.equipeDone(3, 0);
        List<ZoneMines> mines = new ArrayList<>();
        mines.add(ZoneMines.SOLAR_PANEL_5);
        PanneauSolaire firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(true, mines);
        Assertions.assertEquals(6, panneauxSolaire.entryPanneau(firstPanneau, mines).numero());
        mines.add(ZoneMines.SOLAR_PANEL_6);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(true, mines);
        Assertions.assertEquals(4, panneauxSolaire.entryPanneau(firstPanneau, mines).numero());
        mines.add(ZoneMines.SOLAR_PANEL_4);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(true, mines);
        Assertions.assertEquals(3, panneauxSolaire.entryPanneau(firstPanneau, mines).numero());
        mines.add(ZoneMines.SOLAR_PANEL_3);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(mines);
        Assertions.assertNull(panneauxSolaire.entryPanneau(firstPanneau, mines));
    }

    @Test
    void entryPointMinesToutesBloquees() {
        panneauxSolaire.team(Team.BLEU);
        panneauxSolaire.equipeDone(3, 0);
        List<ZoneMines> mines = List.of(
            ZoneMines.SOLAR_PANEL_3,
            ZoneMines.SOLAR_PANEL_4,
            ZoneMines.SOLAR_PANEL_5,
            ZoneMines.SOLAR_PANEL_6,
            ZoneMines.SOLAR_PANEL_7
        );
        PanneauSolaire firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(mines);
        Assertions.assertNull(panneauxSolaire.entryPanneau(firstPanneau, mines));
        panneauxSolaire.team(Team.JAUNE);
        panneauxSolaire.equipeDone(3, 0);
        firstPanneau = panneauxSolaire.nextPanneauSolaireToProcess(true, mines);
        Assertions.assertNull(panneauxSolaire.entryPanneau(firstPanneau, mines));
    }

}
