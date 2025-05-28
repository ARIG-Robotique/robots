package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.BackstageState;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.StrategyOption;
import org.arig.robot.model.Team;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellRobotServosService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

@Slf4j
public class NerellOrdonanceur extends AbstractOrdonanceur {

  @Autowired
  private NerellRobotStatus nerellRobotStatus;

  @Autowired
  private NerellIOService nerellIO;

  @Autowired
  private RobotGroupService pamiTriangleGroupService;

  @Autowired
  private RobotGroupService pamiCarreGroupService;

  @Autowired
  private RobotGroupService pamiRondGroupService;

  @Autowired
  private RobotGroupService pamiStarGroupService;

  @Autowired
  private BaliseService baliseService;

  @Autowired
  private NerellEcranService nerellEcranService;

  @Autowired
  private NerellRobotServosService nerellServos;

  private int getX(int x) {
    return tableUtils.getX(nerellRobotStatus.team() == Team.BLEU, x);
  }

  private int getX(double x) {
    return getX((int) x);
  }

  @Override
  public String getPathfinderMap() {
    return nerellRobotStatus.team().pathfinderMap("nerell");
  }

  @Override
  protected void connectGroups() {
    if (!pamiTriangleGroupService.getGroup().isOpen()) {
      robotStatus.pamiTriangleGroupOk(pamiTriangleGroupService.getGroup().tryConnect());
    }
    if (!pamiCarreGroupService.getGroup().isOpen()) {
      robotStatus.pamiCarreGroupOk(pamiCarreGroupService.getGroup().tryConnect());
    }
    if (!pamiRondGroupService.getGroup().isOpen()) {
      robotStatus.pamiRondGroupOk(pamiRondGroupService.getGroup().tryConnect());
    }

    if (!pamiStarGroupService.getGroup().isOpen()) {
        robotStatus.pamiStarGroupOk(pamiStarGroupService.getGroup().tryConnect());
    }

  }

  @Override
  protected void exitFromScreen() {
    if (ecranService.config() != null && ecranService.config().isExit()) {
      ecranService.displayMessage("Arret du programme");
      if (robotStatus.pamiTriangleGroupOk()) {
        pamiTriangleGroupService.quit();
      }
      if (robotStatus.pamiCarreGroupOk()) {
        pamiCarreGroupService.quit();
      }
      if (robotStatus.pamiRondGroupOk()) {
        pamiRondGroupService.quit();
      }
      if (robotStatus.pamiStarGroupOk()) {
        pamiStarGroupService.quit();
      }
      throw new ExitProgram(true);
    }
  }

  @Override
  public void afterInit() {
    nerellRobotStatus.baliseEnabled(true);
    choixEquipeStrategy();
  }

  @Override
  public void addDeadZones() {

  }

  @Override
  public void beforeMatch(boolean skip) {
    configBalise(nerellRobotStatus.team().name());

    if (!skip) {
      positionStrategy();
    }
    choixConfig();

    if (!skip) {
      try {
        ThreadUtils.sleep(2000);
        mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation(20));
        switch (nerellRobotStatus.strategy()) {
          case QUALIF:
          case FINALE_1:
          case FINALE_2:
            mv.avanceMM(40);
            log.info("Strategy {}. Calage terminé", nerellRobotStatus.strategy().name());
            break;

          default:
            break;
        }


      } catch (Exception e /*AvoidingException e*/) {
        nerellEcranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
        throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
      }
    }

    // Visu après la tirette
    // TODO Envoyer une demande de sound aux PAMIs

  }

  @Override
  public void startMatch() {
    if (!baliseService.isOK()) {
      nerellRobotStatus.baliseEnabled(false);
    }

    if (robotStatus.pamiTriangleGroupOk()) {
      pamiTriangleGroupService.start();
    }
    if (robotStatus.pamiCarreGroupOk()) {
      pamiCarreGroupService.start();
    }
    if (robotStatus.pamiRondGroupOk()) {
      pamiRondGroupService.start();
    }
    if (robotStatus.pamiStarGroupOk()) {
        pamiStarGroupService.start();
    }
  }

  @Override
  public void inMatch() {
    // Nope
  }

  @Override
  public void afterMatch() {
    double currentX = mv.currentXMm();
    double currentY = mv.currentYMm();
    if (nerellRobotStatus.backstage() != BackstageState.TARGET_REACHED) {
      if (nerellRobotStatus.team() == Team.BLEU) {
        if (currentX >= 2350 && currentY >= 1450) {
          nerellRobotStatus.backstage(BackstageState.TARGET_REACHED);
        }
      } else {
        if (currentX <= 650 && currentY >= 1450) {
          nerellRobotStatus.backstage(BackstageState.TARGET_REACHED);
        }
      }
    }

    baliseService.idle();
  }

  @Override
  public void beforePowerOff() {
    if (robotStatus.pamiTriangleGroupOk()) {
      pamiTriangleGroupService.end();
    }
    if (robotStatus.pamiCarreGroupOk()) {
      pamiCarreGroupService.end();
    }
    if (robotStatus.pamiRondGroupOk()) {
      pamiRondGroupService.end();
    }
    if (robotStatus.pamiStarGroupOk()) {
      pamiStarGroupService.end();
    }

    nerellIO.enableAlimServos();

    // TODO : Check capteurs
    nerellServos.groupeBlockColonneAvantOuvert(false);
    nerellServos.groupeBlockColonneArriereOuvert(false);
    nerellServos.becAvantOuvert(false);
    nerellServos.becArriereOuvert(false);

    nerellServos.ascenseurAvantStock(true);
    nerellServos.groupePincesAvantPrise(true);
    nerellServos.groupeDoigtsAvantLache(false);

    nerellServos.ascenseurArriereStock(true);
    nerellServos.groupePincesArrierePrise(true);
    nerellServos.groupeDoigtsArriereLache(false);

    nerellEcranService.displayMessage("FIN - Enlever la tirette quand stock vide.");
    while (io.tirette()) {
      ThreadUtils.sleep(1000);
    }
    servos.homes(true);
  }

  /**
   * Etape du choix de l'équipe/stratégie + config balise
   */
  private void choixEquipeStrategy() {
    nerellEcranService.displayMessage("Choix équipe et lancement calage bordure");

    ChangeFilter<Team> teamChangeFilter = new ChangeFilter<>(null);
    ChangeFilter<Strategy> strategyChangeFilter = new ChangeFilter<>(null);
    ChangeFilter<EcranConfig> configChangeFilter = new ChangeFilter<>(null);

    do {
      exitFromScreen();
      connectGroups();

      if (Boolean.TRUE.equals(teamChangeFilter.filter(nerellEcranService.config().getTeam()))) {
        pamiTriangleGroupService.team(nerellEcranService.config().getTeam());
        pamiCarreGroupService.team(nerellEcranService.config().getTeam());
        pamiRondGroupService.team(nerellEcranService.config().getTeam());
        pamiStarGroupService.team(nerellEcranService.config().getTeam());
        log.info("Team {}", nerellRobotStatus.team().name());
      }

      if (Boolean.TRUE.equals(strategyChangeFilter.filter(nerellEcranService.config().getStrategy()))) {
        pamiTriangleGroupService.strategy(nerellEcranService.config().getStrategy());
        pamiCarreGroupService.strategy(nerellEcranService.config().getStrategy());
        pamiRondGroupService.strategy(nerellEcranService.config().getStrategy());
        pamiStarGroupService.strategy(nerellEcranService.config().getStrategy());
        log.info("Strategy {}", nerellRobotStatus.strategy().name());
      }

      nerellRobotStatus.twoRobots(nerellEcranService.config().isTwoRobots());
      nerellRobotStatus.limiter2Etages(nerellEcranService.config().hasOption(StrategyOption.LIMITER_2_ETAGES.description()));
      nerellRobotStatus.eviterCoteAdverse(nerellEcranService.config().hasOption(StrategyOption.EVITER_COTE_ADVERSE.description()));
      nerellRobotStatus.ejectionCoupDePute(nerellEcranService.config().hasOption(StrategyOption.EJECTION_COUP_DE_PUTE.description()));

      if (Boolean.TRUE.equals(configChangeFilter.filter(nerellEcranService.config()))) {
        if (robotStatus.pamiTriangleGroupOk()) {
          pamiTriangleGroupService.configuration();
        }
        if (robotStatus.pamiCarreGroupOk()) {
          pamiCarreGroupService.configuration();
        }
        if (robotStatus.pamiRondGroupOk()) {
          pamiRondGroupService.configuration();
        }
        if (robotStatus.pamiStarGroupOk()) {
          pamiStarGroupService.configuration();
        }

        log.info("Config {}", nerellEcranService.config());
      }

      ThreadUtils.sleep(200);
    } while (!nerellEcranService.config().isStartCalibration());
  }

  /**
   * Calage sur la bordure
   */
  @Override
  public void calageBordure(boolean skip) {
    if (pamiTriangleGroupService.getGroup().isOpen()) {
      nerellEcranService.displayMessage("Calage Triangle");
      pamiTriangleGroupService.calage();
      //pamiTriangleGroupService.waitInitStep(InitStep.PAMI_TRIANGLE_CALAGE_TERMINE, 5);
    }

    if (pamiCarreGroupService.getGroup().isOpen()) {
      nerellEcranService.displayMessage("Calage Carre");
      pamiCarreGroupService.calage();
      //pamiCarreGroupService.waitInitStep(InitStep.PAMI_CARRE_CALAGE_TERMINE, 5);
    }

    if (pamiRondGroupService.getGroup().isOpen()) {
      nerellEcranService.displayMessage("Calage Rond");
      pamiRondGroupService.calage();
      //pamiRondGroupService.waitInitStep(InitStep.PAMI_ROND_CALAGE_TERMINE, 5);
    }

    if (pamiStarGroupService.getGroup().isOpen()) {
      nerellEcranService.displayMessage("Calage Star");
      pamiStarGroupService.calage();
      //pamiStarGroupService.waitInitStep(InitStep.PAMI_STAR_CALAGE_TERMINE, 5);
    }

    nerellEcranService.displayMessage("Calage bordure");
    try {
      robotStatus.disableAvoidance();
      position.setPt(new Point(
        conv.mmToPulse(getX(500)),
        conv.mmToPulse(1000)
      ));
      position.setAngle(conv.degToPulse(90));

      if (!skip) {
        robotStatus.enableCalage(TypeCalage.AVANT);
        mv.avanceMMSansAngle(300);

        position.getPt().setY(conv.mmToPulse(NerellConstantesConfig.dstCallage));
        position.setAngle(conv.degToPulse(-90));

        mv.reculeMM(70);

        if (nerellRobotStatus.team() == Team.JAUNE) {
          mv.gotoOrientationDeg(180);
        } else {
          mv.gotoOrientationDeg(0);
        }

        robotStatus.enableCalage(TypeCalage.AVANT);
        mv.avanceMM(450);
        robotStatus.enableCalage(TypeCalage.AVANT);
        mv.avanceMMSansAngle(100);

        if (!io.auOk()) {
          nerellEcranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
          throw new ExitProgram(true);
        }

        if (nerellRobotStatus.team() == Team.JAUNE) {
          position.getPt().setX(conv.mmToPulse(NerellConstantesConfig.dstCallage));
          position.setAngle(conv.degToPulse(180));
        } else {
          position.getPt().setX(conv.mmToPulse(EurobotConfig.tableWidth - NerellConstantesConfig.dstCallage));
          position.setAngle(conv.degToPulse(0));
        }

        mv.reculeMM(70);
      }
    } catch (AvoidingException e) {
      nerellEcranService.displayMessage("Erreur lors du calage bordure", LogLevel.ERROR);
      throw new RuntimeException("Impossible de se placer pour le départ", e);
    }
  }

  /**
   * Positionnement en fonction de la stratégie
   */
  private void positionStrategy() {
    nerellEcranService.displayMessage("Mise en place");

    try {
      mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

      switch (nerellRobotStatus.strategy()) {
        case QUALIF:
          mv.gotoPoint(getX(1175), 225);
          mv.gotoOrientationDeg(-90);
          break;
        case FINALE_1:
          // Start ???
        case FINALE_2:
          // Start milieu coté adverse
        default:
          mv.gotoPoint(getX(1175), 225);
          mv.gotoOrientationDeg(-90);
          break;
      }

    } catch (AvoidingException e) {
      nerellEcranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
      throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
    }
  }

  /**
   * Etape du choix des options + config balise
   */
  private void choixConfig() {
    String messageDefault = "Mise de la banderolle (/!\\ ELASTIQUE), puis Tirette";
    nerellEcranService.displayMessage(messageDefault);

    SignalEdgeFilter manuelRisingEdge = new SignalEdgeFilter(nerellEcranService.config().isModeManuel(), Type.RISING);
    SignalEdgeFilter manuelFallingEdge = new SignalEdgeFilter(nerellEcranService.config().isModeManuel(), Type.FALLING);

    boolean manuel = nerellEcranService.config().isModeManuel();

    while (!io.tirette()) {
      exitFromScreen();

      if (Boolean.TRUE.equals(manuelRisingEdge.filter(nerellEcranService.config().isModeManuel()))) {
        manuel = true;
        nerellEcranService.displayMessage("!!!! Mode manuel !!!!");
        startMonitoring();
      } else if (manuel && Boolean.TRUE.equals(manuelFallingEdge.filter(nerellEcranService.config().isModeManuel()))) {
        manuel = false;
        endMonitoring();
        nerellEcranService.displayMessage(messageDefault);
      }

      // Si on est pas en manuel, gestion de la strategy
      if (!manuel && !nerellEcranService.config().isSkipCalageBordure()) {
        nerellEcranService.displayMessage(messageDefault);

        if (robotStatus.pamiTriangleGroupOk()) {
          pamiTriangleGroupService.configuration();
        }
        if (robotStatus.pamiCarreGroupOk()) {
          pamiCarreGroupService.configuration();
        }
        if (robotStatus.pamiRondGroupOk()) {
          pamiRondGroupService.configuration();
        }
        if (robotStatus.pamiStarGroupOk()) {
          pamiStarGroupService.configuration();
        }
      }

      robotStatus.twoRobots(nerellEcranService.config().isTwoRobots());
      avoidingService.setSafeAvoidance(nerellEcranService.config().isSafeAvoidance());

      connectGroups();

      ThreadUtils.sleep(manuel ? 4000 : 200);
    }

    if (robotStatus.pamiTriangleGroupOk()) {
      pamiTriangleGroupService.ready();
    }
    if (robotStatus.pamiCarreGroupOk()) {
      pamiCarreGroupService.ready();
    }
    if (robotStatus.pamiRondGroupOk()) {
      pamiRondGroupService.ready();
    }
    if (robotStatus.pamiStarGroupOk()) {
        pamiStarGroupService.ready();
    }
  }
}
