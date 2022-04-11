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
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.ecran.EcranPhoto;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.services.NerellIOService;
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
    private RobotGroupService groupService;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private NerellEcranService ecranService;

    private int getX(int x) {
        return tableUtils.getX(nerellRobotStatus.team() == Team.VIOLET, x);
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
    public void beforeMatch() {
        positionStrategy();

        // Lancement d'une première lecture de couleurs pour initialiser les capteurs
        nerellIO.enableLedCapteurCouleur();
        ThreadUtils.sleep(NerellConstantesConfig.WAIT_LED);
        nerellIO.couleurVentouseBas();
        nerellIO.couleurVentouseHaut();
        nerellIO.disableLedCapteurCouleur();

        choixConfig();

        if (nerellRobotStatus.etalonageBaliseOk()) {
            nerellRobotStatus.enableBalise();
        }

        // Visu après la tirette
        nerellIO.enableLedCapteurCouleur();
        ThreadUtils.sleep(NerellConstantesConfig.WAIT_LED);
        nerellIO.disableLedCapteurCouleur();
    }

    @Override
    public void startMatch() {
        if (robotStatus.groupOk()) {
            groupService.start();
        }
    }

    @Override
    public void inMatch() {
    }

    @Override
    public void afterMatch() {
        nerellIO.releaseAllPompes();
        baliseService.idle();
        nerellRobotStatus.disableBalise();
    }

    @Override
    public void beforePowerOff() {
        nerellIO.disableAllPompes();
        ThreadUtils.sleep(1000);
    }

    /**
     * Etape du choix de l'équipe/stratégie + config balise
     */
    private void choixEquipeStrategy() {
        ecranService.displayMessage("Choix équipe et lancement calage bordure");

        ChangeFilter<Team> teamChangeFilter = new ChangeFilter<>(null);
        ChangeFilter<Strategy> strategyChangeFilter = new ChangeFilter<>(null);
        SignalEdgeFilter updatePhotoFilter = new SignalEdgeFilter(false, Type.RISING);
        SignalEdgeFilter doEtalonnageFilter = new SignalEdgeFilter(false, Type.RISING);

        do {
            exitFromScreen();
            connectGroup();
            connectBalise();
            configBalise(updatePhotoFilter, doEtalonnageFilter);

            if (teamChangeFilter.filter(ecranService.config().getTeam())) {
                groupService.team(ecranService.config().getTeam());
                log.info("Team {}", nerellRobotStatus.team().name());
            }

            if (strategyChangeFilter.filter(ecranService.config().getStrategy())) {
                groupService.strategy(ecranService.config().getStrategy());
                log.info("Strategy {}", nerellRobotStatus.strategy().name());
            }

            nerellRobotStatus.twoRobots(ecranService.config().isTwoRobots());
            nerellRobotStatus.statuettePresente(ecranService.config().hasOption(EurobotConfig.STATUETTE_PRESENTE));
            nerellRobotStatus.vitrinePresente(ecranService.config().hasOption(EurobotConfig.VITRINE_PRESENTE));

            groupService.configuration();

            ThreadUtils.sleep(200);
        } while (!ecranService.config().isStartCalibration());
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
            if (updatePhotoFilter.filter(ecranService.config().isUpdatePhoto())) {
                // sur front montant de "updatePhoto" on prend une photo et l'envoie à l'écran
                PhotoResponse photo = baliseService.getPhoto();
                EcranPhoto query = new EcranPhoto();
                query.setMessage(photo == null ? "Erreur inconnue" : photo.getErrorMessage());
                query.setPhoto(photo == null ? null : photo.getData());
                ecranService.updatePhoto(query);

            } else if (doEtalonnageFilter.filter(ecranService.config().isEtalonnageBalise())) {
                // sur front montant de "etalonnageBalise" on lance l'étalonnage et envoie le résultat à l'écran
                EtalonnageResponse etalonnage = baliseService.etalonnage();
                EcranPhoto query = new EcranPhoto();
                query.setMessage(etalonnage == null ? "Erreur inconnue" : etalonnage.getErrorMessage());
                query.setPhoto(etalonnage == null ? null : etalonnage.getData());
                ecranService.updatePhoto(query);
            }

            nerellRobotStatus.etalonageBaliseOk(ecranService.config().isEtalonnageOk());
        } else {
            nerellRobotStatus.etalonageBaliseOk(false);
        }
    }

    /**
     * Calage sur la bordure
     */
    @Override
    public void calageBordure(boolean skip) {
        ecranService.displayMessage("Calage bordure");

        groupService.calage();

        try {
            robotStatus.disableAvoidance();
            position.setPt(new Point(
                    conv.mmToPulse(getX(NerellConstantesConfig.dstCallage)),
                    conv.mmToPulse(1180)
            ));
            if (nerellRobotStatus.team() == Team.JAUNE) {
                position.setAngle(conv.degToPulse(0));
            } else {
                position.setAngle(conv.degToPulse(180));
            }
            if (!skip) {
                robotStatus.enableCalageBordure(TypeCalage.ARRIERE);
                mv.reculeMMSansAngle(300);

                position.getPt().setX(conv.mmToPulse(getX(NerellConstantesConfig.dstCallage)));
                if (nerellRobotStatus.team() == Team.JAUNE) {
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setAngle(conv.degToPulse(180));
                }

                mv.avanceMM(150);
                mv.gotoOrientationDeg(-90);

                ecranService.displayMessage("Attente Odin devant la galerie");
                groupService.waitInitStep(InitStep.ODIN_DEVANT_GALERIE); // Odin calé, en attente devant la galerie

                robotStatus.enableCalageBordure(TypeCalage.ARRIERE);
                mv.reculeMMSansAngle(1000);

                if (!io.auOk()) {
                    ecranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
                    throw new ExitProgram(true);
                }

                groupService.initStep(InitStep.NERELL_CALAGE_TERMINE); // Nerell calé

                position.getPt().setY(conv.mmToPulse(2000 - NerellConstantesConfig.dstCallage));
                position.setAngle(conv.degToPulse(-90));

                mv.avanceMM(150);
            }
        } catch (AvoidingException e) {
            ecranService.displayMessage("Erreur lors du calage bordure", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer pour le départ", e);
        }
    }

    /**
     * Positionnement en fonction de la stratégie
     */
    public void positionStrategy() {
        ecranService.displayMessage("Attente Odin en position depart");
        groupService.waitInitStep(InitStep.ODIN_EN_POSITION_BASIC);
        ecranService.displayMessage("Mise en place");

        try {
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            switch (nerellRobotStatus.strategy()) {
                default:
                    mv.gotoPoint(getX(270), 1440);
                    if (nerellRobotStatus.team() == Team.JAUNE) {
                        mv.gotoOrientationDeg(0);
                    } else {
                        mv.gotoOrientationDeg(180);
                    }
                    groupService.initStep(InitStep.NERELL_EN_POSITION_BASIC);
                    break;
            }
        } catch (AvoidingException e) {
            ecranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
        }
    }

    /**
     * Etape du choix des options + config balise
     */
    private void choixConfig() {
        ecranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");

        SignalEdgeFilter manuelRisingEdge = new SignalEdgeFilter(ecranService.config().isModeManuel(), Type.RISING);
        SignalEdgeFilter manuelFallingEdge = new SignalEdgeFilter(ecranService.config().isModeManuel(), Type.FALLING);
        SignalEdgeFilter updatePhotoFilter = new SignalEdgeFilter(false, Type.RISING);
        SignalEdgeFilter doEtalonnageFilter = new SignalEdgeFilter(false, Type.RISING);

        boolean manuel = ecranService.config().isModeManuel();

        while (!io.tirette()) {
            exitFromScreen();

            if (manuelRisingEdge.filter(ecranService.config().isModeManuel())) {
                manuel = true;
                ecranService.displayMessage("!!!! Mode manuel !!!!");
                startMonitoring();
            } else if (manuel && manuelFallingEdge.filter(ecranService.config().isModeManuel())) {
                manuel = false;
                endMonitoring();
                ecranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");
            }

            // Si on est pas en manuel, gestion de la strategy
            if (!manuel && !ecranService.config().isSkipCalageBordure()) {
                ecranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");

                groupService.configuration();
            }

            robotStatus.twoRobots(ecranService.config().isTwoRobots());
            avoidingService.setSafeAvoidance(ecranService.config().isSafeAvoidance());

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
