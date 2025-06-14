package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.constants.PamiConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.AngleRange;
import org.arig.robot.model.InitStep;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.StrategyOption;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.PamiEcranService;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.services.PamiRobotServosService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.system.leds.ARIG2025IoPamiLeds;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PamiOrdonanceur extends AbstractOrdonanceur {

  @Autowired
  private PamiRobotStatus pamiRobotStatus;

  @Autowired
  private PamiIOService pamiIOService;

  @Autowired
  private RobotGroupService groupService;

  @Autowired
  private PamiEcranService pamiEcranService;

  @Autowired
  private PamiRobotServosService pamiServosService;

  @Autowired
  private ARIG2025IoPamiLeds leds;

  private int getX(int x) {
    return tableUtils.getX(pamiRobotStatus.team() == Team.BLEU, x);
  }

  private int getX(double x) {
    return getX((int) x);
  }

  @Override
  public String getPathfinderMap() {
    return pamiRobotStatus.team().pathfinderMap("pamis");
  }

  @Override
  protected void connectGroups() {
    if (!groupService.getGroup().isOpen()) {
      robotStatus.robotGroupOk(groupService.getGroup().tryConnect());
    }
  }

  @Override
  protected void initRun() {
    leds.setAllLeds(ARIG2025IoPamiLeds.LedColor.Red);
  }

  @Override
  protected void initLidar() {
    super.initLidar();
    lidar.setConfiguration(true, -20.23, 10, 500);
    lidar.setSensorOrigin(70, 0);
    lidar.addAnglesFiltered(AngleRange.builder().minDeg(70).maxDeg(180).build());
    lidar.addAnglesFiltered(AngleRange.builder().minDeg(-180).maxDeg(-70).build());
    lidar.setCouloirYMm((double) 110 / 2);
  }

  @Override
  protected void startLidar() {
    log.info("Pas de start scan Lidar sur les D500 des PAMIs !");
  }

  @Override
  protected void exitFromScreen() {
    super.exitFromScreen();
    if (groupService.isQuit()) {
      ecranService.displayMessage("Arret du programme depuis Nerell");
      throw new ExitProgram(true);
    }
  }

  @Override
  protected void customWaitAu() {
    pamiIOService.sound();
  }

  @Override
  public void afterInit() {
    choixEquipeStrategy();
  }

  @Override
  public void addDeadZones() {
  }

  @Override
  public void beforeMatch(boolean skip) {
    if (!skip) {
      positionStrategy();
    }
    choixConfig();
  }

  @Override
  protected boolean waitTirette() {
    return robotStatus.robotGroupOk() ? !groupService.isStart() : io.tirette();
  }

  @Override
  public void startMatch() {
    // Nope
    leds.setAllLeds(ARIG2025IoPamiLeds.LedColor.White);
  }

  @Override
  public void inMatch() {
    if (robotStatus.getRemainingTime() <= EurobotConfig.pamiStartRemainingTimeMs && !pamiRobotStatus.showTime()) {
      log.info("Start show time animation \\°/");
      pamiRobotStatus.enableShowTime();
    }
  }

  @Override
  public void afterMatch() {
  }

  @Override
  public void beforePowerOff() {
    pamiServosService.handFerme(true);
    leds.setAllLeds(ARIG2025IoPamiLeds.LedColor.Red);
    ThreadUtils.sleep(2000);
  }

  /**
   * Etape du choix de l'équipe/stratégie
   */
  private void choixEquipeStrategy() {
    ChangeFilter<Team> teamChangeFilter = new ChangeFilter<>(null);
    ChangeFilter<Strategy> strategyChangeFilter = new ChangeFilter<>(null);
    ChangeFilter<Boolean> groupChangeFilter = new ChangeFilter<>(null);

    leds.setAllLeds(ARIG2025IoPamiLeds.LedColor.Black);
    leds.setLedAU(ARIG2025IoPamiLeds.LedColor.Green);

    boolean done;
    do {
      exitFromScreen();
      connectGroups();

      if (Boolean.TRUE.equals(groupChangeFilter.filter(robotStatus.robotGroupOk()))) {
        if (robotStatus.robotGroupOk()) {
          pamiEcranService.displayMessage("Attente configuration Nerell");
        } else {
          pamiEcranService.displayMessage("Choix équipe et lancement calage bordure");
        }
      }

      if (robotStatus.robotGroupOk()) {
        done = groupService.isCalage();

      } else {
        if (Boolean.TRUE.equals(teamChangeFilter.filter(pamiEcranService.config().getTeam()))) {
          pamiRobotStatus.team(pamiEcranService.config().getTeam());
          log.info("Team {}", pamiRobotStatus.team().name());
        }

        if (Boolean.TRUE.equals(strategyChangeFilter.filter(pamiEcranService.config().getStrategy()))) {
          pamiRobotStatus.strategy(pamiEcranService.config().getStrategy());
          log.info("Strategy {}", pamiRobotStatus.strategy().name());
        }

        pamiRobotStatus.twoRobots(pamiEcranService.config().isTwoRobots());
        pamiRobotStatus.limiter2Etages(pamiEcranService.config().hasOption(StrategyOption.LIMITER_2_ETAGES.description()));
        pamiRobotStatus.eviterCoteAdverse(pamiEcranService.config().hasOption(StrategyOption.EVITER_COTE_ADVERSE.description()));
        pamiRobotStatus.ejectionCoupDePute(pamiEcranService.config().hasOption(StrategyOption.EJECTION_COUP_DE_PUTE.description()));

        done = pamiEcranService.config().isStartCalibration();
      }

      if (pamiRobotStatus.team() != null) {
        leds.setLedTeam(pamiRobotStatus.team() == Team.JAUNE ? ARIG2025IoPamiLeds.LedColor.Yellow : ARIG2025IoPamiLeds.LedColor.Blue);
      }

      ThreadUtils.sleep(1000);
    } while (!done);

    leds.setLedCalage(ARIG2025IoPamiLeds.LedColor.Red);
  }

  /**
   * Calage sur la bordure
   */
  @Override
  public void calageBordure(boolean skip) {
    pamiEcranService.displayMessage("Calage bordure");

    try {
      robotStatus.disableAvoidance();

      position.setPt(new Point(
        conv.mmToPulse(getX(200)),
        conv.mmToPulse(1775)
      ));
      if (pamiRobotStatus.team() == Team.JAUNE) {
        position.setAngle(conv.degToPulse(0));
      } else {
        position.setAngle(conv.degToPulse(180));
      }

      if (!skip) {
        robotStatus.enableCalageTempo(1000, TypeCalage.ARRIERE);
        mv.reculeMMSansAngle(300);

        RobotName.RobotIdentification id = robotName.id();
        double x = getX(PamiConstantesConfig.dstCallageArriere);
        if (id == RobotName.RobotIdentification.PAMI_TRIANGLE) {
          position.setPt(new Point(
            conv.mmToPulse(x),
            conv.mmToPulse(1940)
          ));
          groupService.initStep(InitStep.PAMI_TRIANGLE_CALAGE_TERMINE);

        } else if (id == RobotName.RobotIdentification.PAMI_CARRE) {
          position.setPt(new Point(
            conv.mmToPulse(x),
            conv.mmToPulse(1830)
          ));
          groupService.initStep(InitStep.PAMI_CARRE_CALAGE_TERMINE);

        } else if (id == RobotName.RobotIdentification.PAMI_ROND) {
          position.setPt(new Point(
            conv.mmToPulse(x),
            conv.mmToPulse(1720)
          ));
          groupService.initStep(InitStep.PAMI_ROND_CALAGE_TERMINE);

        } else {
          position.setPt(new Point(
            conv.mmToPulse(x),
            conv.mmToPulse(1610)
          ));
          groupService.initStep(InitStep.PAMI_STAR_CALAGE_TERMINE);
        }

        if (pamiRobotStatus.team() == Team.JAUNE) {
          position.setAngle(conv.degToPulse(0));
        } else {
          position.setAngle(conv.degToPulse(180));
        }

        if (!io.auOk()) {
          pamiEcranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
          throw new ExitProgram(true);
        }
      }

      leds.setLedCalage(ARIG2025IoPamiLeds.LedColor.Green);
    } catch (AvoidingException e) {
      pamiEcranService.displayMessage("Erreur lors du calage bordure", LogLevel.ERROR);
      throw new RuntimeException("Impossible de se placer pour le départ", e);
    }
  }

  /**
   * Positionnement en fonction de la stratégie
   */
  public void positionStrategy() {
    pamiEcranService.displayMessage("Mise en place");

    try {
      mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            /*switch (pamiRobotStatus.strategy()) {
                case FINALE_1:
                case FINALE_2:
                case BASIC:
                default:
                    if (robotStatus.twoRobots()) {
                        mv.gotoPoint(getX(240), 1740);
                        mv.gotoPoint(getX(570), 1740);
                        //groupService.initStep(InitStep.ODIN_DEVANT_GALERIE); // Odin calé, en attente devant la galerie
                        mv.gotoPoint(getX(570), 1130);
                        //pamiEcranService.displayMessage("Attente calage Nerell");
                        groupService.waitInitStep(InitStep.NERELL_CALAGE_TERMINE); // Attente Nerell calé
                        //pamiEcranService.displayMessage("Mise en place");
                        mv.gotoPoint(getX(230), 1140);
                        mv.alignFrontTo(getX(345), 537); // Entry point abri de chantier
                    } else {
                        //pamiEcranService.displayMessage("Mise en place");
                        mv.gotoPoint(getX(240), 1430);
                        mv.alignFrontTo(getX(800), 1700);
                    }
                    //groupService.initStep(InitStep.ODIN_EN_POSITION);
                    break;
            }*/
    } catch (/*AvoidingException*/ Exception e) {
      pamiEcranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
      throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
    }
  }

  /**
   * Etape du choix des options
   */
  private void choixConfig() {
    if (robotStatus.robotGroupOk()) {
      pamiEcranService.displayMessage("Attente démarrage Nerell");

      while (!groupService.isReady()) {
        exitFromScreen();
        robotStatus.twoRobots(true);
        avoidingService.setSafeAvoidance(pamiEcranService.config().isSafeAvoidance());
        ThreadUtils.sleep(200);
      }

    } else {
      pamiEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");

      SignalEdgeFilter manuelRisingEdge = new SignalEdgeFilter(pamiEcranService.config().isModeManuel(), Type.RISING);
      SignalEdgeFilter manuelFallingEdge = new SignalEdgeFilter(pamiEcranService.config().isModeManuel(), Type.FALLING);

      boolean manuel = pamiEcranService.config().isModeManuel();

      while (!io.tirette()) {
        exitFromScreen();

        if (Boolean.TRUE.equals(manuelRisingEdge.filter(pamiEcranService.config().isModeManuel()))) {
          manuel = true;
          pamiEcranService.displayMessage("!!!! Mode manuel !!!!");
          startMonitoring();
        } else if (manuel && Boolean.TRUE.equals(manuelFallingEdge.filter(pamiEcranService.config().isModeManuel()))) {
          manuel = false;
          endMonitoring();
          pamiEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");
        }

        // Si on est pas en manuel, gestion de la config
        if (!manuel && !pamiEcranService.config().isSkipCalageBordure()) {
          pamiEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");
        }

        robotStatus.twoRobots(pamiEcranService.config().isTwoRobots());
        avoidingService.setSafeAvoidance(pamiEcranService.config().isSafeAvoidance());

        ThreadUtils.sleep(manuel ? 4000 : 200);
      }
    }

    // Sound screen vérrouillé
    leds.setAllLeds(ARIG2025IoPamiLeds.LedColor.Green);
    for (int i = 0; i < 10; i++) {
      pamiIOService.sound();
      ThreadUtils.sleep(300);
    }
  }

  @Override
  protected void cycleFin() {
    ecranService.displayMessage(
      String.format("FIN - Tirette Nerell et AU OK pour fin - Score %s",
        robotStatus.calculerPoints())
    );

    while (!groupService.isEnd() || !io.auOk()) {
      if (!io.auOk()) {
        pamiRobotStatus.disableShowTime();
      } else {
        pamiRobotStatus.enableShowTime();
      }

      ThreadUtils.sleep(1000);
    }
    pamiRobotStatus.disableShowTime();

    ecranService.displayMessage("FIN - Extinction");
    ThreadUtils.sleep(500);

    beforePowerOff(); // impl
  }
}
