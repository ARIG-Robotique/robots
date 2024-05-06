package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.Bras;
import org.arig.robot.model.InitStep;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.SiteDeCharge;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellRobotServosService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.strategy.actions.active.robot.PanneauSolaireEquipeAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

import java.util.stream.Stream;

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
    private BaliseService baliseService;

    @Autowired
    private NerellEcranService nerellEcranService;

    @Autowired
    private BrasService bras;

    @Autowired
    private NerellRobotServosService nerellServos;

    private int getX(int x) {
        return tableUtils.getX(nerellRobotStatus.team() == Team.JAUNE, x);
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
    }

    @Override
    protected void exitFromScreen() {
        if (ecranService.config() != null && ecranService.config().isExit()) {
            ecranService.displayMessage("Arret du programme");
            pamiTriangleGroupService.quit();
            pamiCarreGroupService.quit();
            pamiRondGroupService.quit();
            throw new ExitProgram(true);
        }
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
                    case FINALE_1:
                    case FINALE_2:
                        mv.tourneDeg(180);
                        mv.gotoPoint(getX(260), 1430);
                        mv.alignBackTo(getX(750), 1550);
                        break;

                    case BASIC:
                    default:
                        //mv.tourneDeg(180);
                        break;
                }


            } catch (AvoidingException e) {
                nerellEcranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
                throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
            }
        }

        // Visu après la tirette
        // TODO Envoyer une demande de sound aux PAMIs

    }

    @Override
    public void startMatch() {
        if (robotStatus.pamiTriangleGroupOk()) {
            pamiTriangleGroupService.start();
        }
        if (robotStatus.pamiCarreGroupOk()) {
            pamiCarreGroupService.start();
        }
        if (robotStatus.pamiRondGroupOk()) {
            pamiRondGroupService.start();
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
        if (nerellRobotStatus.team() == Team.BLEU) {
            // Nord
            if (nerellRobotStatus.siteDeDepart() != SiteDeCharge.BLEU_NORD && currentX <= 500 && currentY >= 1500) {
                nerellRobotStatus.siteDeCharge(SiteDeCharge.BLEU_NORD);
            }
            // Milieu
            if (nerellRobotStatus.siteDeDepart() != SiteDeCharge.BLEU_MILIEU && currentX >= 2500 && currentY >= 500 && currentY <= 1500) {
                nerellRobotStatus.siteDeCharge(SiteDeCharge.BLEU_MILIEU);
            }
            // Sud
            if (nerellRobotStatus.siteDeDepart() != SiteDeCharge.BLEU_SUD && currentX <= 500 && currentY <= 500) {
                nerellRobotStatus.siteDeCharge(SiteDeCharge.BLEU_SUD);
            }
        } else {
            // Nord
            if (nerellRobotStatus.siteDeDepart() != SiteDeCharge.JAUNE_NORD && currentX >= 2500 && currentY >= 1500) {
                nerellRobotStatus.siteDeCharge(SiteDeCharge.JAUNE_NORD);
            }
            // Milieu
            if (nerellRobotStatus.siteDeDepart() != SiteDeCharge.JAUNE_MILIEU && currentX <= 500 && currentY >= 500 && currentY <= 1500) {
                nerellRobotStatus.siteDeCharge(SiteDeCharge.JAUNE_MILIEU);
            }
            // Sud
            if (nerellRobotStatus.siteDeDepart() != SiteDeCharge.JAUNE_SUD && currentX >= 2500 && currentY <= 500) {
                nerellRobotStatus.siteDeCharge(SiteDeCharge.JAUNE_SUD);
            }
        }

        baliseService.idle();
    }

    @Override
    public void beforePowerOff() {
        pamiTriangleGroupService.end();
        pamiCarreGroupService.end();
        pamiRondGroupService.end();

        nerellIO.enableAlimServos();

        Stream.of(Bras.values()).forEach(b -> {
            bras.setBras(b, new PointBras(194, 104, -90, null), 30, false);
        });
        nerellServos.groupePinceAvantOuvert(false);
        nerellServos.groupePinceArriereOuvert(false);

        nerellEcranService.displayMessage("FIN - Enlever la tirette quand stock vide.");
        while (io.tirette()) {
            ThreadUtils.sleep(1000);
        }
        servos.homes();
    }

    /**
     * Etape du choix de l'équipe/stratégie + config balise
     */
    private void choixEquipeStrategy() {
        nerellEcranService.displayMessage("Choix équipe et lancement calage bordure");

        ChangeFilter<Team> teamChangeFilter = new ChangeFilter<>(null);
        ChangeFilter<Strategy> strategyChangeFilter = new ChangeFilter<>(null);
        SignalEdgeFilter updatePhotoFilter = new SignalEdgeFilter(false, Type.RISING);
        SignalEdgeFilter doEtalonnageFilter = new SignalEdgeFilter(false, Type.RISING);

        do {
            exitFromScreen();
            connectGroups();

            if (Boolean.TRUE.equals(teamChangeFilter.filter(nerellEcranService.config().getTeam()))) {
                pamiTriangleGroupService.team(nerellEcranService.config().getTeam());
                pamiCarreGroupService.team(nerellEcranService.config().getTeam());
                pamiRondGroupService.team(nerellEcranService.config().getTeam());
                log.info("Team {}", nerellRobotStatus.team().name());
            }

            if (Boolean.TRUE.equals(strategyChangeFilter.filter(nerellEcranService.config().getStrategy()))) {
                pamiTriangleGroupService.strategy(nerellEcranService.config().getStrategy());
                pamiCarreGroupService.strategy(nerellEcranService.config().getStrategy());
                pamiRondGroupService.strategy(nerellEcranService.config().getStrategy());
                log.info("Strategy {}", nerellRobotStatus.strategy().name());
            }

            nerellRobotStatus.twoRobots(nerellEcranService.config().isTwoRobots());
            nerellRobotStatus.preferePanneaux(nerellEcranService.config().hasOption(EurobotConfig.PREFERE_PANNEAUX));
            nerellRobotStatus.activeVolAuSol(nerellEcranService.config().hasOption(EurobotConfig.ACTIVE_VOL_AU_SOL));
            nerellRobotStatus.activeVolJardinieres(nerellEcranService.config().hasOption(EurobotConfig.ACTIVE_VOL_JARDINIERES));

            pamiTriangleGroupService.configuration();
            pamiCarreGroupService.configuration();
            pamiRondGroupService.configuration();

            ThreadUtils.sleep(200);
        } while (!nerellEcranService.config().isStartCalibration());
    }

    /**
     * Calage sur la bordure
     */
    @Override
    public void calageBordure(boolean skip) {
        nerellEcranService.displayMessage("Calage bordure");

        pamiTriangleGroupService.calage();
        pamiCarreGroupService.calage();
        pamiRondGroupService.calage();

        try {
            robotStatus.disableAvoidance();
            position.setPt(new Point(
                conv.mmToPulse(getX(500)),
                conv.mmToPulse(1000)
            ));
            if (nerellRobotStatus.team() == Team.BLEU) {
                position.setAngle(conv.degToPulse(0));
            } else {
                position.setAngle(conv.degToPulse(180));
            }
            if (!skip) {
                robotStatus.enableCalageBordure(TypeCalage.ARRIERE);
                mv.reculeMMSansAngle(300);

                position.getPt().setX(conv.mmToPulse(getX(NerellConstantesConfig.dstCallage)));
                if (nerellRobotStatus.team() == Team.BLEU) {
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setAngle(conv.degToPulse(180));
                }

                mv.avanceMM(70);

                if (nerellRobotStatus.strategy() == Strategy.BASIC) {
                    mv.gotoOrientationDeg(-90);
                    bras.setBrasAvant(PositionBras.CALLAGE_PANNEAUX);
                } else {
                    mv.gotoOrientationDeg(90);
                }

                robotStatus.enableCalageBordure(TypeCalage.AVANT);
                mv.avanceMM(1000);
                robotStatus.enableCalageBordure(TypeCalage.AVANT);
                mv.avanceMMSansAngle(100);

                if (!io.auOk()) {
                    nerellEcranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
                    throw new ExitProgram(true);
                }

                if (nerellRobotStatus.strategy() == Strategy.BASIC) {
                    position.getPt().setY(conv.mmToPulse(NerellConstantesConfig.dstCallage));
                    position.setAngle(conv.degToPulse(-90));
                } else {
                    position.getPt().setY(conv.mmToPulse(EurobotConfig.tableHeight - NerellConstantesConfig.dstCallage));
                    position.setAngle(conv.degToPulse(90));
                }

                mv.reculeMM(70);

                bras.setBrasAvant(PositionBras.INIT);

                pamiTriangleGroupService.initStep(InitStep.NERELL_CALAGE_TERMINE); // Nerell calé
                pamiCarreGroupService.initStep(InitStep.NERELL_CALAGE_TERMINE); // Nerell calé
                pamiRondGroupService.initStep(InitStep.NERELL_CALAGE_TERMINE); // Nerell calé
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
                case BASIC:
                    mv.gotoPoint(getX(PanneauSolaireEquipeAction.ENTRY_X), PanneauSolaireEquipeAction.WORK_Y);
                    mv.gotoOrientationDeg(180);
                    pamiTriangleGroupService.initStep(InitStep.NERELL_EN_POSITION);
                    pamiCarreGroupService.initStep(InitStep.NERELL_EN_POSITION);
                    pamiRondGroupService.initStep(InitStep.NERELL_EN_POSITION);
                    nerellRobotStatus.siteDeDepart(nerellRobotStatus.team() == Team.BLEU ? SiteDeCharge.BLEU_SUD : SiteDeCharge.JAUNE_SUD);
                    break;
                case FINALE_1:
                    // Start ???
                case FINALE_2:
                    // Start milieu coté adverse
                case AGGRESSIVE:
                    // Start plant (nord)
                default:
                    mv.gotoPoint(getX(240), 1775);
                    mv.alignFrontTo(getX(1220), 1490);
                    pamiTriangleGroupService.initStep(InitStep.NERELL_EN_POSITION);
                    pamiCarreGroupService.initStep(InitStep.NERELL_EN_POSITION);
                    pamiRondGroupService.initStep(InitStep.NERELL_EN_POSITION);
                    nerellRobotStatus.siteDeDepart(nerellRobotStatus.team() == Team.BLEU ? SiteDeCharge.BLEU_NORD : SiteDeCharge.JAUNE_NORD);
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
        nerellEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");

        SignalEdgeFilter manuelRisingEdge = new SignalEdgeFilter(nerellEcranService.config().isModeManuel(), Type.RISING);
        SignalEdgeFilter manuelFallingEdge = new SignalEdgeFilter(nerellEcranService.config().isModeManuel(), Type.FALLING);
        SignalEdgeFilter updatePhotoFilter = new SignalEdgeFilter(false, Type.RISING);
        SignalEdgeFilter doEtalonnageFilter = new SignalEdgeFilter(false, Type.RISING);

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
                nerellEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");
            }

            // Si on est pas en manuel, gestion de la strategy
            if (!manuel && !nerellEcranService.config().isSkipCalageBordure()) {
                nerellEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");

                pamiTriangleGroupService.configuration();
                pamiCarreGroupService.configuration();
                pamiRondGroupService.configuration();
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
    }
}
