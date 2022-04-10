package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZoneDeFouilleTest {

    private ZoneDeFouille zoneDeFouille;

    @BeforeEach
    void setUp() {
        zoneDeFouille = new ZoneDeFouille();
    }

    @Test
    void testInit() {
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, zoneDeFouille.get(2).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(3).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(6).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(7).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(8).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, zoneDeFouille.get(9).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(10).couleur());

        Assertions.assertEquals(0, zoneDeFouille.score());
    }

    @Test
    void testMatchJauneObstacle() {
        zoneDeFouille.team(Team.JAUNE);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        cf.incrementTry();
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        Assertions.assertEquals(0, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(3, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(10, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern1(false);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(10, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        Assertions.assertFalse(zoneDeFouille.hasInconnu());
        assertPattern1(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertFalse(zoneDeFouille.isComplete());

        cf = zoneDeFouille.nextCarreFouilleToProcess(1);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(1);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern1() {
        zoneDeFouille.team(Team.JAUNE);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern1(false);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(15, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        Assertions.assertFalse(zoneDeFouille.hasInconnu());
        assertPattern1(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern1Reverse() {
        zoneDeFouille.team(Team.JAUNE);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(0, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(3, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(15, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        Assertions.assertFalse(zoneDeFouille.hasInconnu());
        assertPattern1(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern1() {
        zoneDeFouille.team(Team.VIOLET);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern1(false);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern1(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern1Reverse() {
        zoneDeFouille.team(Team.VIOLET);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(8, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(15, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern1(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0, true);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern2() {
        zoneDeFouille.team(Team.JAUNE);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(0, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern2(false);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(3, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern2(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern2() {
        zoneDeFouille.team(Team.VIOLET);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(0, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
       assertPattern2(false);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(8, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        Assertions.assertEquals(15, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern2(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern3() {
        zoneDeFouille.team(Team.JAUNE);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern3(false);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern3(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern3() {
        zoneDeFouille.team(Team.VIOLET);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern3(false);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.JAUNE);
        Assertions.assertEquals(15, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern3(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchJaunePattern4() {
        zoneDeFouille.team(Team.JAUNE);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(1, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(0, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern4(false);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(2, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(3, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(15, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern4(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(5, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(6, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testMatchVioletPattern4() {
        zoneDeFouille.team(Team.VIOLET);

        CarreFouille cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(10, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.INTERDIT);
        Assertions.assertEquals(0, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern4(false);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(9, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(10, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(8, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(15, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(7, cf.numero());
        Assertions.assertTrue(cf.needRead());
        cf.couleur(CouleurCarreFouille.VIOLET);
        cf.bascule(true);
        Assertions.assertEquals(20, zoneDeFouille.score());

        zoneDeFouille.refreshProcessing();
        assertPattern4(true);

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertEquals(4, cf.numero());
        Assertions.assertFalse(cf.needRead());
        cf.bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        cf = zoneDeFouille.nextCarreFouilleToProcess(0);
        Assertions.assertNull(cf);
        Assertions.assertTrue(zoneDeFouille.isComplete());
    }

    @Test
    void testScoreJaune() {
        zoneDeFouille.team(Team.JAUNE);

        zoneDeFouille.get(1).couleur(CouleurCarreFouille.JAUNE).bascule(true);
        zoneDeFouille.get(2).couleur(CouleurCarreFouille.JAUNE).bascule(true);
        zoneDeFouille.get(3).couleur(CouleurCarreFouille.INTERDIT);
        zoneDeFouille.get(4).couleur(CouleurCarreFouille.VIOLET);
        zoneDeFouille.get(5).couleur(CouleurCarreFouille.JAUNE).bascule(true);
        zoneDeFouille.get(6).couleur(CouleurCarreFouille.JAUNE).bascule(true);
        zoneDeFouille.get(7).couleur(CouleurCarreFouille.VIOLET);
        zoneDeFouille.get(8).couleur(CouleurCarreFouille.INTERDIT);
        zoneDeFouille.get(9).couleur(CouleurCarreFouille.VIOLET);
        zoneDeFouille.get(10).couleur(CouleurCarreFouille.VIOLET);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(4).bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(7).bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(8).bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(3).bascule(true);
        Assertions.assertEquals(0, zoneDeFouille.score());
    }

    @Test
    void testScoreViolet() {
        zoneDeFouille.team(Team.VIOLET);

        zoneDeFouille.get(1).couleur(CouleurCarreFouille.JAUNE);
        zoneDeFouille.get(2).couleur(CouleurCarreFouille.JAUNE);
        zoneDeFouille.get(3).couleur(CouleurCarreFouille.INTERDIT);
        zoneDeFouille.get(4).couleur(CouleurCarreFouille.VIOLET).bascule(true);
        zoneDeFouille.get(5).couleur(CouleurCarreFouille.JAUNE);
        zoneDeFouille.get(6).couleur(CouleurCarreFouille.JAUNE);
        zoneDeFouille.get(7).couleur(CouleurCarreFouille.VIOLET).bascule(true);
        zoneDeFouille.get(8).couleur(CouleurCarreFouille.INTERDIT);
        zoneDeFouille.get(9).couleur(CouleurCarreFouille.VIOLET).bascule(true);
        zoneDeFouille.get(10).couleur(CouleurCarreFouille.VIOLET).bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(6).bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(3).bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(2).bascule(true);
        Assertions.assertEquals(25, zoneDeFouille.score());

        zoneDeFouille.get(8).bascule(true);
        Assertions.assertEquals(0, zoneDeFouille.score());

    }

    // -------------------------------------------------------------- //

    private void assertPatternCommenceEnCouleur() {
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, zoneDeFouille.get(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, zoneDeFouille.get(2).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, zoneDeFouille.get(3).couleur());

        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, zoneDeFouille.get(8).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, zoneDeFouille.get(9).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, zoneDeFouille.get(10).couleur());
    }

    private void assertPatternCommenceParInterdit() {
        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, zoneDeFouille.get(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, zoneDeFouille.get(2).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, zoneDeFouille.get(3).couleur());

        Assertions.assertEquals(CouleurCarreFouille.VIOLET, zoneDeFouille.get(8).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, zoneDeFouille.get(9).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, zoneDeFouille.get(10).couleur());
    }

    private void assertPatternMilieuInconnu() {
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(6).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INCONNU, zoneDeFouille.get(7).couleur());
    }

    private void assertPatternMilieuCommenceParJaune() {
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, zoneDeFouille.get(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, zoneDeFouille.get(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, zoneDeFouille.get(6).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, zoneDeFouille.get(7).couleur());
    }

    private void assertPatternMilieuCommenceParViolet() {
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, zoneDeFouille.get(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, zoneDeFouille.get(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, zoneDeFouille.get(6).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, zoneDeFouille.get(7).couleur());
    }

    private void assertPattern1(boolean full) {
        if (full) {
            Assertions.assertFalse(zoneDeFouille.hasInconnu());
            assertPatternMilieuCommenceParViolet();
        } else {
            Assertions.assertTrue(zoneDeFouille.hasInconnu());
            assertPatternMilieuInconnu();
        }
        assertPatternCommenceEnCouleur();
    }
    private void assertPattern2(boolean full) {
        if (full) {
            Assertions.assertFalse(zoneDeFouille.hasInconnu());
            assertPatternMilieuCommenceParJaune();
        } else {
            Assertions.assertTrue(zoneDeFouille.hasInconnu());
            assertPatternMilieuInconnu();
        }
        assertPatternCommenceParInterdit();
    }
    private void assertPattern3(boolean full) {
        if (full) {
            Assertions.assertFalse(zoneDeFouille.hasInconnu());
            assertPatternMilieuCommenceParJaune();
        } else {
            Assertions.assertTrue(zoneDeFouille.hasInconnu());
            assertPatternMilieuInconnu();
        }
        assertPatternCommenceEnCouleur();
    }
    private void assertPattern4(boolean full) {
        if (full) {
            Assertions.assertFalse(zoneDeFouille.hasInconnu());
            assertPatternMilieuCommenceParViolet();
        } else {
            Assertions.assertTrue(zoneDeFouille.hasInconnu());
            assertPatternMilieuInconnu();
        }
        assertPatternCommenceParInterdit();
    }
}
