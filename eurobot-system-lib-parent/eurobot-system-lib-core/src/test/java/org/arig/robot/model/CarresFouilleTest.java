package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarresFouilleTest {

    private CarresFouille carresFouille;

    @BeforeEach
    void setUp() {
        carresFouille = new CarresFouille();
    }

    @Test
    void testInit() {
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, carresFouille.get(2).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(3).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(6).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(7).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(8).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, carresFouille.get(9).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(10).couleur());

        Assertions.assertEquals(0, carresFouille.score());
    }

    @Test
    void testScoreTeamBourrinAuMilieuJaune() {
        carresFouille.team(Team.JAUNE);

        for (int i = 4 ; i <= 7 ; i++) {
            CarreFouille cf = carresFouille.get(i);
            cf.bascule(true);
        }

        Assertions.assertEquals(15, carresFouille.score());
        carresFouille.get(1).couleur(CouleurCarreFouille.JAUNE).bascule(true);
        carresFouille.get(2).bascule(true);
        carresFouille.refreshProcessing();
        assertPattern1(false);

        Assertions.assertEquals(25, carresFouille.score());
    }

    @Test
    void testScoreTeamBourrinAuMilieuViolet() {
        carresFouille.team(Team.VIOLET);

        for (int i = 4 ; i <= 7 ; i++) {
            CarreFouille cf = carresFouille.get(i);
            cf.bascule(true);
        }

        Assertions.assertEquals(15, carresFouille.score());
        carresFouille.get(10).couleur(CouleurCarreFouille.VIOLET).bascule(true);
        carresFouille.get(9).bascule(true);
        carresFouille.refreshProcessing();
        assertPattern1(false);

        Assertions.assertEquals(25, carresFouille.score());
    }

    @Test
    void testMatchJauneObstacle() {
        carresFouille.team(Team.JAUNE);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0);
        cf.incrementTry();
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        Assertions.assertEquals(0, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(3, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(10, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern1(false);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(10, carresFouille.score());

        carresFouille.refreshProcessing();
        Assertions.assertFalse(carresFouille.hasInconnu());
        assertPattern1(true);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertFalse(carresFouille.isComplete());

        cf = carresFouille.nextCarreFouilleToProcess(1);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(1);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern1() {
        carresFouille.team(Team.JAUNE);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern1(false);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(15, carresFouille.score());

        carresFouille.refreshProcessing();
        Assertions.assertFalse(carresFouille.hasInconnu());
        assertPattern1(true);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern1Reverse() {
        carresFouille.team(Team.JAUNE);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(0, carresFouille.score());

        carresFouille.refreshProcessing();

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(3, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(15, carresFouille.score());

        carresFouille.refreshProcessing();
        Assertions.assertFalse(carresFouille.hasInconnu());
        assertPattern1(true);

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern1() {
        carresFouille.team(Team.VIOLET);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern1(false);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern1(true);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern1Reverse() {
        carresFouille.team(Team.VIOLET);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        carresFouille.refreshProcessing();

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(8, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(15, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern1(true);

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern2() {
        carresFouille.team(Team.JAUNE);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(0, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern2(false);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(3, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern2(true);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern2() {
        carresFouille.team(Team.VIOLET);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(0, carresFouille.score());

        carresFouille.refreshProcessing();
       assertPattern2(false);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(8, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        Assertions.assertEquals(15, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern2(true);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern3() {
        carresFouille.team(Team.JAUNE);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern3(false);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern3(true);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern3() {
        carresFouille.team(Team.VIOLET);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern3(false);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        Assertions.assertEquals(15, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern3(true);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern4() {
        carresFouille.team(Team.JAUNE);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(0, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern4(false);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(3, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(15, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern4(true);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern4() {
        carresFouille.team(Team.VIOLET);

        CarreFouille cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(0, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern4(false);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(8, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(20, carresFouille.score());

        carresFouille.refreshProcessing();
        assertPattern4(true);

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        cf = carresFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(carresFouille.isComplete());
    }

    @Test
    void testScoreJaune() {
        carresFouille.team(Team.JAUNE);

        carresFouille.get(1).couleur(CouleurCarreFouille.JAUNE).bascule(true);
        carresFouille.get(2).couleur(CouleurCarreFouille.JAUNE).bascule(true);
        carresFouille.get(3).couleur(CouleurCarreFouille.INTERDIT);
        carresFouille.get(4).couleur(CouleurCarreFouille.VIOLET);
        carresFouille.get(5).couleur(CouleurCarreFouille.JAUNE).bascule(true);
        carresFouille.get(6).couleur(CouleurCarreFouille.JAUNE).bascule(true);
        carresFouille.get(7).couleur(CouleurCarreFouille.VIOLET);
        carresFouille.get(8).couleur(CouleurCarreFouille.INTERDIT);
        carresFouille.get(9).couleur(CouleurCarreFouille.VIOLET);
        carresFouille.get(10).couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(25, carresFouille.score());

        carresFouille.get(4).bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        carresFouille.get(7).bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        carresFouille.get(8).bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        carresFouille.get(3).bascule(true);
        Assertions.assertEquals(0, carresFouille.score());
    }

    @Test
    void testScoreViolet() {
        carresFouille.team(Team.VIOLET);

        carresFouille.get(1).couleur(CouleurCarreFouille.JAUNE);
        carresFouille.get(2).couleur(CouleurCarreFouille.JAUNE);
        carresFouille.get(3).couleur(CouleurCarreFouille.INTERDIT);
        carresFouille.get(4).couleur(CouleurCarreFouille.VIOLET).bascule(true);
        carresFouille.get(5).couleur(CouleurCarreFouille.JAUNE);
        carresFouille.get(6).couleur(CouleurCarreFouille.JAUNE);
        carresFouille.get(7).couleur(CouleurCarreFouille.VIOLET).bascule(true);
        carresFouille.get(8).couleur(CouleurCarreFouille.INTERDIT);
        carresFouille.get(9).couleur(CouleurCarreFouille.VIOLET).bascule(true);
        carresFouille.get(10).couleur(CouleurCarreFouille.VIOLET).bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        carresFouille.get(6).bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        carresFouille.get(3).bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        carresFouille.get(2).bascule(true);
        Assertions.assertEquals(25, carresFouille.score());

        carresFouille.get(8).bascule(true);
        Assertions.assertEquals(0, carresFouille.score());

    }

    // -------------------------------------------------------------- //

    private void assertPatternCommenceEnCouleur() {
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, carresFouille.get(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, carresFouille.get(2).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, carresFouille.get(3).couleur());

        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, carresFouille.get(8).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, carresFouille.get(9).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, carresFouille.get(10).couleur());
    }

    private void assertPatternCommenceParInterdit() {
        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, carresFouille.get(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, carresFouille.get(2).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, carresFouille.get(3).couleur());

        Assertions.assertEquals(CouleurCarreFouille.VIOLET, carresFouille.get(8).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, carresFouille.get(9).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, carresFouille.get(10).couleur());
    }

    private void assertPatternMilieuInconnu() {
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(6).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, carresFouille.get(7).couleur());
    }

    private void assertPatternMilieuCommenceParJaune() {
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, carresFouille.get(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, carresFouille.get(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, carresFouille.get(6).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, carresFouille.get(7).couleur());
    }

    private void assertPatternMilieuCommenceParViolet() {
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, carresFouille.get(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, carresFouille.get(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, carresFouille.get(6).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, carresFouille.get(7).couleur());
    }

    private void assertPattern1(boolean full) {
        if (full) {
            Assertions.assertFalse(carresFouille.hasInconnu());
            assertPatternMilieuCommenceParViolet();
        } else {
            Assertions.assertTrue(carresFouille.hasInconnu());
            assertPatternMilieuInconnu();
        }
        assertPatternCommenceEnCouleur();
    }
    private void assertPattern2(boolean full) {
        if (full) {
            Assertions.assertFalse(carresFouille.hasInconnu());
            assertPatternMilieuCommenceParJaune();
        } else {
            Assertions.assertTrue(carresFouille.hasInconnu());
            assertPatternMilieuInconnu();
        }
        assertPatternCommenceParInterdit();
    }
    private void assertPattern3(boolean full) {
        if (full) {
            Assertions.assertFalse(carresFouille.hasInconnu());
            assertPatternMilieuCommenceParJaune();
        } else {
            Assertions.assertTrue(carresFouille.hasInconnu());
            assertPatternMilieuInconnu();
        }
        assertPatternCommenceEnCouleur();
    }
    private void assertPattern4(boolean full) {
        if (full) {
            Assertions.assertFalse(carresFouille.hasInconnu());
            assertPatternMilieuCommenceParViolet();
        } else {
            Assertions.assertTrue(carresFouille.hasInconnu());
            assertPatternMilieuInconnu();
        }
        assertPatternCommenceParInterdit();
    }
}
