package org.arig.robot.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.InitStep;
import org.arig.robot.model.Point;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.TestEurobotStatus;
import org.arig.robot.system.RobotGroupOverSocket;
import org.arig.robot.utils.ThreadUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.BindException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

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

  @BeforeAll
  public static void init() {
    Assumptions.assumeTrue(System.getenv("CI") == null, "Run on CI. Tests skipped.");
  }

  @BeforeEach
  @SneakyThrows
  void setUp() {
    int primaryPort = 9090;
    int secondaryPort = 9095;

    int nbTry = 3;
    while (nbTry-- >= 0) {
      try {
        statusPrimary = new TestEurobotStatus(true);
        rgPrimary = new RobotGroupOverSocket(statusPrimary, AbstractRobotStatus::robotGroupOk, primaryPort, "localhost", secondaryPort, executor);
        rgPrimary.openSocket();
        rgServicePrimary = new RobotGroupService(statusPrimary, rgPrimary, executor);


        statusSecondary = new TestEurobotStatus(false);
        rgSecondary = new RobotGroupOverSocket(statusSecondary, AbstractRobotStatus::robotGroupOk, secondaryPort, "localhost", primaryPort, executor);
        rgSecondary.openSocket();
        rgServiceSecondary = new RobotGroupService(statusSecondary, rgSecondary, executor);

        statusPrimary.robotGroupOk(rgPrimary.tryConnect());
        statusSecondary.robotGroupOk(rgSecondary.tryConnect());
        break;
      } catch (BindException e) {
        log.error("Error in setUp", e);
        ThreadUtils.sleep(2000);
      }

      primaryPort++;
      secondaryPort++;
    }
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
    Assertions.assertEquals(Strategy.QUALIF, statusPrimary.strategy());
    Assertions.assertEquals(Strategy.QUALIF, statusSecondary.strategy());

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

  @ParameterizedTest
  @MethodSource("testCurrentPositionData")
  void testCurrentPosition(int x, int y) {
    rgServicePrimary.setCurrentPosition(x, y);
    ThreadUtils.sleep(WAIT);

    Assertions.assertEquals(statusSecondary.otherPosition(), new Point(x, y));
  }

  private static Stream<Arguments> testCurrentPositionData() {
    return Stream.of(
      Arguments.of(0, 0),
      Arguments.of(1000, 1500),
      Arguments.of(2000, 3000)
    );
  }
}
