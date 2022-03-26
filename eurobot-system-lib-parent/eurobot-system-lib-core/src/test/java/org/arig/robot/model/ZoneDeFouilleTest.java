package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ZoneDeFouilleTest {

    private ZoneDeFouille zoneDeFouille;

    @Test
    void testScoreBase() {
        zoneDeFouille = new ZoneDeFouille();

        zoneDeFouille.refreshTeam(Team.JAUNE);
        Assertions.assertEquals(0, zoneDeFouille.score());

        zoneDeFouille.refreshTeam(Team.VIOLET);
        Assertions.assertEquals(0, zoneDeFouille.score());
    }

    @Test
    void testScoreJaune() {
        zoneDeFouille = new ZoneDeFouille();
        zoneDeFouille.refreshTeam(Team.JAUNE);

        zoneDeFouille.get(1).couleur(CouleurCarreeFouille.JAUNE).bascule(true);
        zoneDeFouille.get(2).couleur(CouleurCarreeFouille.JAUNE).bascule(true);
        zoneDeFouille.get(3).couleur(CouleurCarreeFouille.INTERDIT);
        zoneDeFouille.get(4).couleur(CouleurCarreeFouille.VIOLET);
        zoneDeFouille.get(5).couleur(CouleurCarreeFouille.JAUNE).bascule(true);
        zoneDeFouille.get(6).couleur(CouleurCarreeFouille.JAUNE).bascule(true);
        zoneDeFouille.get(7).couleur(CouleurCarreeFouille.VIOLET);
        zoneDeFouille.get(8).couleur(CouleurCarreeFouille.INTERDIT);
        zoneDeFouille.get(9).couleur(CouleurCarreeFouille.VIOLET);
        zoneDeFouille.get(10).couleur(CouleurCarreeFouille.VIOLET);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(4).bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(8).bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(3).bascule(true);
        Assertions.assertEquals(0, zoneDeFouille.score());
    }

}
