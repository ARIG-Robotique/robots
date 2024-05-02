package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.InitStep;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.SiteDeCharge;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.ecran.EcranPhoto;
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

@Slf4j
public class NerellOrdonanceur extends AbstractOrdonanceur {

    @Autowired
    private NerellRobotStatus nerellRobotStatus;

    @Autowired
    private NerellIOService nerellIO;

    @Autowired
    private RobotGroupService groupService;

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

        if (nerellRobotStatus.etalonageBaliseOk()) {
            nerellRobotStatus.enableBalise();
        }

        // Visu après la tirette
        // TODO Envoyer une demande de sound aux PAMIs

    }

    @Override
    public void startMatch() {
        if (robotStatus.groupOk()) {
            groupService.start();
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
        nerellRobotStatus.disableBalise();
    }

    @Override
    public void beforePowerOff() {
        nerellIO.enableAlimServos();

        bras.setBrasAvant(new PointBras(194, 104, -90, null));
        bras.setBrasArriere(new PointBras(194, 104, -90, null));
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
            connectGroup();
            connectBalise();
            configBalise(updatePhotoFilter, doEtalonnageFilter);

            if (Boolean.TRUE.equals(teamChangeFilter.filter(nerellEcranService.config().getTeam()))) {
                groupService.team(nerellEcranService.config().getTeam());
                log.info("Team {}", nerellRobotStatus.team().name());
            }

            if (Boolean.TRUE.equals(strategyChangeFilter.filter(nerellEcranService.config().getStrategy()))) {
                groupService.strategy(nerellEcranService.config().getStrategy());
                log.info("Strategy {}", nerellRobotStatus.strategy().name());
            }

            nerellRobotStatus.twoRobots(nerellEcranService.config().isTwoRobots());
            nerellRobotStatus.preferePanneaux(nerellEcranService.config().hasOption(EurobotConfig.PREFERE_PANNEAUX));
            nerellRobotStatus.activeVolAuSol(nerellEcranService.config().hasOption(EurobotConfig.ACTIVE_VOL_AU_SOL));
            nerellRobotStatus.activeVolJardinieres(nerellEcranService.config().hasOption(EurobotConfig.ACTIVE_VOL_JARDINIERES));

            groupService.configuration();

            ThreadUtils.sleep(200);
        } while (!nerellEcranService.config().isStartCalibration());
    }

    /**
     * Tente de se connecter à la balise ou envoie un heartbeat
     */
    private void connectBalise() {
        if (!baliseService.isConnected()) {
            baliseService.tryConnect();
        } else {
            baliseService.heartbeat();
        }
    }

    /**
     * Prend en compte la config de la balise
     */
    private void configBalise(SignalEdgeFilter updatePhotoFilter, SignalEdgeFilter doEtalonnageFilter) {
        if (baliseService.isConnected()) {
            if (Boolean.TRUE.equals(updatePhotoFilter.filter(nerellEcranService.config().isUpdatePhoto()))) {
                // sur front montant de "updatePhoto" on prend une photo et l'envoie à l'écran
                PhotoResponse photo = baliseService.getPhoto();
                EcranPhoto query = new EcranPhoto();
                query.setMessage(photo == null ? "Erreur inconnue" : photo.getErrorMessage());
                query.setPhoto(photo == null ? null : photo.getData());
                nerellEcranService.updatePhoto(query);

            } else if (Boolean.TRUE.equals(doEtalonnageFilter.filter(nerellEcranService.config().isEtalonnageBalise()))) {
                // sur front montant de "etalonnageBalise" on lance l'étalonnage et envoie le résultat à l'écran
                EtalonnageResponse etalonnage = baliseService.etalonnage();
                EcranPhoto query = new EcranPhoto();
                query.setMessage(etalonnage == null ? "Erreur inconnue" : etalonnage.getErrorMessage());
                query.setPhoto(etalonnage == null ? null : etalonnage.getData());
                nerellEcranService.updatePhoto(query);
            }

            nerellRobotStatus.etalonageBaliseOk(nerellEcranService.config().isEtalonnageOk());
        } else {
            nerellRobotStatus.etalonageBaliseOk(false);
        }
    }

    /**
     * Calage sur la bordure
     */
    @Override
    public void calageBordure(boolean skip) {
        nerellEcranService.displayMessage("Calage bordure");

        groupService.calage();

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

                groupService.initStep(InitStep.NERELL_CALAGE_TERMINE); // Nerell calé
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
                    groupService.initStep(InitStep.NERELL_EN_POSITION);
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
                    groupService.initStep(InitStep.NERELL_EN_POSITION);
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

                groupService.configuration();
            }

            robotStatus.twoRobots(nerellEcranService.config().isTwoRobots());
            avoidingService.setSafeAvoidance(nerellEcranService.config().isSafeAvoidance());

            connectGroup();
            connectBalise();
            configBalise(updatePhotoFilter, doEtalonnageFilter);

            ThreadUtils.sleep(manuel ? 4000 : 200);
        }

        if (robotStatus.groupOk()) {
            groupService.ready();
        }
    }
}
