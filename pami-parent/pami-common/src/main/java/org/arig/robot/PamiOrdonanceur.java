package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.constants.PamiConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.InitStep;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.StrategyOption;
import org.arig.robot.model.Team;
import org.arig.robot.services.PamiEcranService;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.services.PamiRobotServosService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.system.leds.ARIG2024IoPamiLeds;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

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
    private ARIG2024IoPamiLeds leds;

    private int getX(int x) {
        return tableUtils.getX(pamiRobotStatus.team() == Team.JAUNE, x);
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
        leds.setAllLeds(ARIG2024IoPamiLeds.LedColor.Red);
    }

    @Override
    protected void startLidar() {
        log.info("Pas de Lidar sur un pami !");
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
        leds.setAllLeds(ARIG2024IoPamiLeds.LedColor.White);
    }

    @Override
    public void inMatch() {
        if (robotStatus.getRemainingTime() < EurobotConfig.pamiStartRemainingTimeMs) {
            // TODO MANAGE HAND MOVEMENT
        }
    }

    @Override
    public void afterMatch() {
    }

    @Override
    public void beforePowerOff() {
        pamiServosService.handFerme(true);
        leds.setAllLeds(ARIG2024IoPamiLeds.LedColor.Red);
    }

    /**
     * Etape du choix de l'équipe/stratégie
     */
    private void choixEquipeStrategy() {
        ChangeFilter<Team> teamChangeFilter = new ChangeFilter<>(null);
        ChangeFilter<Strategy> strategyChangeFilter = new ChangeFilter<>(null);
        ChangeFilter<Boolean> groupChangeFilter = new ChangeFilter<>(null);

        leds.setAllLeds(ARIG2024IoPamiLeds.LedColor.Black);
        leds.setLedAU(ARIG2024IoPamiLeds.LedColor.Green);

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
                pamiRobotStatus.limit2Etages(pamiEcranService.config().hasOption(StrategyOption.LIMIT_2_ETAGES.name()));

                done = pamiEcranService.config().isStartCalibration();
            }

            if (pamiRobotStatus.team() != null) {
                leds.setLedTeam(pamiRobotStatus.team() == Team.JAUNE ? ARIG2024IoPamiLeds.LedColor.Yellow : ARIG2024IoPamiLeds.LedColor.Blue);
            }

            ThreadUtils.sleep(1000);
        } while (!done);

        leds.setLedCalage(ARIG2024IoPamiLeds.LedColor.Red);
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
                    conv.mmToPulse(getX(1500)),
                    conv.mmToPulse(1000)
            ));
            position.setAngle(conv.degToPulse(-90));

            if (!skip) {
                robotStatus.enableCalageTempo(2000);
                mv.reculeMMSansAngle(300);

                RobotName.RobotIdentification id = robotName.id();
                if (id == RobotName.RobotIdentification.PAMI_TRIANGLE) {
                    position.setPt(new Point(
                        conv.mmToPulse(getX((EurobotConfig.tableWidth / 2.0) - PamiConstantesConfig.dstCallageCote)),
                        conv.mmToPulse(EurobotConfig.tableHeight - PamiConstantesConfig.dstCallageArriere)
                    ));
                    groupService.initStep(InitStep.PAMI_TRIANGLE_CALAGE_TERMINE);

                } else if (id == RobotName.RobotIdentification.PAMI_CARRE) {
                    position.setPt(new Point(
                        conv.mmToPulse(getX(1275)),
                        conv.mmToPulse(EurobotConfig.tableHeight - PamiConstantesConfig.dstCallageArriere)
                    ));
                    groupService.initStep(InitStep.PAMI_CARRE_CALAGE_TERMINE);

                } else {
                    position.setPt(new Point(
                        conv.mmToPulse(getX(1050 + PamiConstantesConfig.dstCallageCote)),
                        conv.mmToPulse(EurobotConfig.tableHeight - PamiConstantesConfig.dstCallageArriere)
                    ));
                    groupService.initStep(InitStep.PAMI_ROND_CALAGE_TERMINE);
                }

                position.setAngle(conv.degToPulse(-90));

                if (!io.auOk()) {
                    pamiEcranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
                    throw new ExitProgram(true);
                }
            }

            leds.setLedCalage(ARIG2024IoPamiLeds.LedColor.Green);
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
        leds.setAllLeds(ARIG2024IoPamiLeds.LedColor.Green);
        for (int i = 0 ; i < 10 ; i++) {
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
            ThreadUtils.sleep(1000);
        }

        ecranService.displayMessage("FIN - Extinction");
        ThreadUtils.sleep(500);

        beforePowerOff(); // impl
    }
}
