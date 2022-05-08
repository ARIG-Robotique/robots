package org.arig.robot.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.*;
import org.arig.robot.system.RobotGroupOverSocket;
import org.arig.robot.utils.ThreadUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
class RobotGroupServiceTest {

    private static final int WAIT = 500;

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    private EurobotStatus statusPrimary;
    private RobotGroupOverSocket rgPrimary;
    private RobotGroupService rgServicePrimary;

    private EurobotStatus statusSecondary;
    private RobotGroupOverSocket rgSecondary;
    private RobotGroupService rgServiceSecondary;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        statusPrimary = new TestEurobotStatus(true);
        rgPrimary = new RobotGroupOverSocket(statusPrimary, 9090, "localhost", 9091, executor);
        rgPrimary.openSocket();
        rgServicePrimary = new RobotGroupService(statusPrimary, rgPrimary, executor);


        statusSecondary = new TestEurobotStatus(false);
        rgSecondary = new RobotGroupOverSocket(statusSecondary, 9091, "localhost", 9090, executor);
        rgSecondary.openSocket();
        rgServiceSecondary = new RobotGroupService(statusSecondary, rgSecondary, executor);

        statusPrimary.groupOk(rgPrimary.tryConnect());
        statusSecondary.groupOk(rgSecondary.tryConnect());
    }

    @AfterEach
    void tearDown() {
        log.info("TearDown");
        rgSecondary.end();
        rgPrimary.end();
    }

    @Test
    void testCalage() {
        Assertions.assertFalse(rgServicePrimary.isCalage());
        Assertions.assertFalse(rgServiceSecondary.isCalage());

        rgServicePrimary.calage();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(rgServicePrimary.isCalage());
        Assertions.assertTrue(rgServiceSecondary.isCalage());
    }

    @ParameterizedTest
    @EnumSource(InitStep.class)
    void testInitStep(InitStep s) {
        Assertions.assertEquals(0, rgServicePrimary.getInitStep());
        Assertions.assertEquals(0, rgServiceSecondary.getInitStep());

        rgServicePrimary.initStep(s);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(s.step(), rgServicePrimary.getInitStep());
        Assertions.assertEquals(s.step(), rgServiceSecondary.getInitStep());
    }


    @Test
    void testReady() {
        Assertions.assertFalse(rgServicePrimary.isReady());
        Assertions.assertFalse(rgServiceSecondary.isReady());

        rgServicePrimary.ready();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(rgServicePrimary.isReady());
        Assertions.assertTrue(rgServiceSecondary.isReady());
    }

    @Test
    void testStart() {
        Assertions.assertFalse(rgServicePrimary.isStart());
        Assertions.assertFalse(rgServiceSecondary.isStart());

        rgServicePrimary.start();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(rgServicePrimary.isStart());
        Assertions.assertTrue(rgServiceSecondary.isStart());
    }

    @ParameterizedTest
    @EnumSource(Team.class)
    void testTeam(Team team) {
        Assertions.assertNull(statusPrimary.team());
        Assertions.assertNull(statusSecondary.team());

        rgServicePrimary.team(team);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(team, statusPrimary.team());
        Assertions.assertEquals(team, statusSecondary.team());
    }

    @ParameterizedTest
    @EnumSource(Strategy.class)
    void testStrategy(Strategy strategy) {
        Assertions.assertEquals(Strategy.BASIC, statusPrimary.strategy());
        Assertions.assertEquals(Strategy.BASIC, statusSecondary.strategy());

        rgServicePrimary.strategy(strategy);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(strategy, statusPrimary.strategy());
        Assertions.assertEquals(strategy, statusSecondary.strategy());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"Dépose campement", "Dépose galerie", "Prise statuette"})
    void testCurrentAction(String action) {
        rgServicePrimary.setCurrentAction(action);
        rgServiceSecondary.setCurrentAction(action);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(action, statusPrimary.otherCurrentAction());
        Assertions.assertEquals(action, statusSecondary.otherCurrentAction());
    }

    @Test
    void testConfigurationReverseCarreDeFouille() {
        Assertions.assertFalse(statusPrimary.reverseCarreDeFouille());
        Assertions.assertFalse(statusSecondary.reverseCarreDeFouille());

        statusPrimary.reverseCarreDeFouille(true);
        rgServicePrimary.configuration();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.reverseCarreDeFouille());
        Assertions.assertTrue(statusSecondary.reverseCarreDeFouille());
    }

    @Test
    void testDistributeurEquipePris() {
        Assertions.assertFalse(statusPrimary.distributeurEquipePris());
        Assertions.assertFalse(statusSecondary.distributeurEquipePris());

        rgServicePrimary.distributeurEquipePris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.distributeurEquipePris());
        Assertions.assertTrue(statusSecondary.distributeurEquipePris());
    }

    @Test
    void testDistributeurCommunEquipePris() {
        Assertions.assertFalse(statusPrimary.distributeurCommunEquipePris());
        Assertions.assertFalse(statusSecondary.distributeurCommunEquipePris());

        rgServicePrimary.distributeurCommunEquipePris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.distributeurCommunEquipePris());
        Assertions.assertTrue(statusSecondary.distributeurCommunEquipePris());
    }

    @Test
    void testDistributeurCommunAdversePris() {
        Assertions.assertFalse(statusPrimary.distributeurCommunAdversePris());
        Assertions.assertFalse(statusSecondary.distributeurCommunAdversePris());

        rgServicePrimary.distributeurCommunAdversePris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.distributeurCommunAdversePris());
        Assertions.assertTrue(statusSecondary.distributeurCommunAdversePris());
    }

    @Test
    void testSiteEchantillonPris() {
        Assertions.assertFalse(statusPrimary.siteEchantillonPris());
        Assertions.assertFalse(statusSecondary.siteEchantillonPris());

        rgServicePrimary.siteEchantillonPris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.siteEchantillonPris());
        Assertions.assertTrue(statusSecondary.siteEchantillonPris());
    }

    @Test
    void testSiteEchantillonAdversePris() {
        Assertions.assertFalse(statusPrimary.siteEchantillonAdversePris());
        Assertions.assertFalse(statusSecondary.siteEchantillonAdversePris());

        rgServicePrimary.siteEchantillonAdversePris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.siteEchantillonAdversePris());
        Assertions.assertTrue(statusSecondary.siteEchantillonAdversePris());
    }

    @Test
    void testSiteDeFouillePris() {
        Assertions.assertFalse(statusPrimary.siteDeFouillePris());
        Assertions.assertFalse(statusSecondary.siteDeFouillePris());

        rgServicePrimary.siteDeFouillePris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.siteDeFouillePris());
        Assertions.assertTrue(statusSecondary.siteDeFouillePris());
    }

    @Test
    void testSiteDeFouilleAdversePris() {
        Assertions.assertFalse(statusPrimary.siteDeFouilleAdversePris());
        Assertions.assertFalse(statusSecondary.siteDeFouilleAdversePris());

        rgServicePrimary.siteDeFouilleAdversePris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.siteDeFouilleAdversePris());
        Assertions.assertTrue(statusSecondary.siteDeFouilleAdversePris());
    }

    @Test
    void testVitrineActive() {
        Assertions.assertFalse(statusPrimary.vitrineActive());
        Assertions.assertFalse(statusSecondary.vitrineActive());

        rgServicePrimary.vitrineActive();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.vitrineActive());
        Assertions.assertTrue(statusSecondary.vitrineActive());
    }

    @Test
    void testStatuettePris() {
        Assertions.assertFalse(statusPrimary.statuettePrise());
        Assertions.assertFalse(statusPrimary.statuettePriseDansCeRobot());
        Assertions.assertFalse(statusSecondary.statuettePrise());
        Assertions.assertFalse(statusSecondary.statuettePriseDansCeRobot());

        rgServicePrimary.statuettePris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.statuettePrise());
        Assertions.assertTrue(statusPrimary.statuettePriseDansCeRobot());
        Assertions.assertTrue(statusSecondary.statuettePrise());
        Assertions.assertFalse(statusSecondary.statuettePriseDansCeRobot());
    }

    @Test
    void testStatuetteDansVitrine() {
        Assertions.assertFalse(statusPrimary.statuetteDansVitrine());
        Assertions.assertFalse(statusPrimary.statuettePrise());
        Assertions.assertFalse(statusSecondary.statuetteDansVitrine());
        Assertions.assertFalse(statusSecondary.statuettePrise());

        rgServicePrimary.statuetteDansVitrine();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.statuetteDansVitrine());
        Assertions.assertTrue(statusPrimary.statuettePrise());
        Assertions.assertTrue(statusSecondary.statuetteDansVitrine());
        Assertions.assertTrue(statusSecondary.statuettePrise());
    }

    @Test
    void testRepliqueDepose() {
        Assertions.assertFalse(statusPrimary.repliqueDepose());
        Assertions.assertFalse(statusSecondary.repliqueDepose());

        rgServicePrimary.repliqueDepose();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.repliqueDepose());
        Assertions.assertTrue(statusSecondary.repliqueDepose());
    }

    @Test
    void testEchantillonAbriChantierDistributeurPris() {
        Assertions.assertFalse(statusPrimary.echantillonAbriChantierDistributeurPris());
        Assertions.assertFalse(statusSecondary.echantillonAbriChantierDistributeurPris());

        rgServicePrimary.echantillonAbriChantierDistributeurPris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.echantillonAbriChantierDistributeurPris());
        Assertions.assertTrue(statusSecondary.echantillonAbriChantierDistributeurPris());
    }

    @Test
    void testEchantillonAbriChantierCarreFouillePris() {
        Assertions.assertFalse(statusPrimary.echantillonAbriChantierCarreFouillePris());
        Assertions.assertFalse(statusSecondary.echantillonAbriChantierCarreFouillePris());

        rgServicePrimary.echantillonAbriChantierCarreFouillePris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.echantillonAbriChantierCarreFouillePris());
        Assertions.assertTrue(statusSecondary.echantillonAbriChantierCarreFouillePris());
    }

    @Test
    void testEchantillonCampementPris() {
        Assertions.assertFalse(statusPrimary.echantillonCampementPris());
        Assertions.assertFalse(statusSecondary.echantillonCampementPris());

        rgServicePrimary.echantillonCampementPris();
        ThreadUtils.sleep(WAIT);

        Assertions.assertTrue(statusPrimary.echantillonCampementPris());
        Assertions.assertTrue(statusSecondary.echantillonCampementPris());
    }

    @ParameterizedTest
    @EnumSource(SiteDeRetour.class)
    void testSiteDeRetour(SiteDeRetour siteDeRetour) {
        Assertions.assertEquals(SiteDeRetour.AUCUN, statusPrimary.siteDeRetour());
        Assertions.assertEquals(SiteDeRetour.AUCUN, statusPrimary.siteDeRetourAutreRobot());
        Assertions.assertEquals(SiteDeRetour.AUCUN, statusSecondary.siteDeRetour());
        Assertions.assertEquals(SiteDeRetour.AUCUN, statusSecondary.siteDeRetourAutreRobot());

        rgServicePrimary.siteDeRetour(siteDeRetour);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(siteDeRetour, statusPrimary.siteDeRetour());
        Assertions.assertEquals(SiteDeRetour.AUCUN, statusPrimary.siteDeRetourAutreRobot());
        Assertions.assertEquals(SiteDeRetour.AUCUN, statusSecondary.siteDeRetour());
        Assertions.assertEquals(siteDeRetour, statusSecondary.siteDeRetourAutreRobot());

        rgServiceSecondary.siteDeRetour(siteDeRetour);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(siteDeRetour, statusPrimary.siteDeRetour());
        Assertions.assertEquals(siteDeRetour, statusPrimary.siteDeRetourAutreRobot());
        Assertions.assertEquals(siteDeRetour, statusSecondary.siteDeRetour());
        Assertions.assertEquals(siteDeRetour, statusSecondary.siteDeRetourAutreRobot());
    }

    @Test
    void testCouleurCarreFouille1Jaune4Jaune() {
        rgServicePrimary.team(Team.JAUNE);

        rgServicePrimary.couleurCarreFouille(1, CouleurCarreFouille.JAUNE);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusPrimary.carreFouille(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, statusPrimary.carreFouille(3).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusSecondary.carreFouille(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, statusPrimary.carreFouille(3).couleur());

        rgServicePrimary.couleurCarreFouille(4, CouleurCarreFouille.JAUNE);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusPrimary.carreFouille(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, statusPrimary.carreFouille(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusSecondary.carreFouille(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, statusSecondary.carreFouille(5).couleur());
    }

    @Test
    void testCouleurCarreFouille1Interdit4Violet() {
        rgServicePrimary.team(Team.JAUNE);

        rgServicePrimary.couleurCarreFouille(1, CouleurCarreFouille.INTERDIT);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, statusPrimary.carreFouille(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusPrimary.carreFouille(3).couleur());
        Assertions.assertEquals(CouleurCarreFouille.INTERDIT, statusSecondary.carreFouille(1).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusSecondary.carreFouille(3).couleur());

        rgServicePrimary.couleurCarreFouille(4, CouleurCarreFouille.VIOLET);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(CouleurCarreFouille.VIOLET, statusPrimary.carreFouille(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusPrimary.carreFouille(5).couleur());
        Assertions.assertEquals(CouleurCarreFouille.VIOLET, statusSecondary.carreFouille(4).couleur());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusSecondary.carreFouille(5).couleur());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 7})
    void testBasculePattern1Jaune(int numero) {
        rgServicePrimary.team(Team.JAUNE);

        rgServicePrimary.couleurCarreFouille(numero, CouleurCarreFouille.JAUNE);
        rgServicePrimary.basculeCarreFouille(numero);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusPrimary.carreFouille(numero).couleur());
        Assertions.assertTrue(statusPrimary.carreFouille(numero).bascule());
        Assertions.assertEquals(CouleurCarreFouille.JAUNE, statusSecondary.carreFouille(numero).couleur());
        Assertions.assertTrue(statusSecondary.carreFouille(numero).bascule());
    }

    @Test
    void testDeposeAbriChantier() {
        Assertions.assertEquals(0, statusPrimary.abriChantierSize());
        Assertions.assertEquals(0, statusSecondary.abriChantierSize());

        rgServicePrimary.deposeAbriChantier(CouleurEchantillon.VERT, CouleurEchantillon.ROUGE);
        ThreadUtils.sleep(WAIT);

        Assertions.assertEquals(2, statusPrimary.abriChantierSize());
        Assertions.assertEquals(2, statusSecondary.abriChantierSize());
        Assertions.assertEquals(CouleurEchantillon.VERT, statusPrimary.abriChantierEchantillon(0));
        Assertions.assertEquals(CouleurEchantillon.ROUGE, statusSecondary.abriChantierEchantillon(1));
    }
}
