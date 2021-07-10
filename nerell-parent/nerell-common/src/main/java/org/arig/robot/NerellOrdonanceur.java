package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.INerellConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.ecran.UpdatePhotoInfos;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.services.NerellServosService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

@Slf4j
public class NerellOrdonanceur extends AbstractOrdonanceur {

    @Autowired
    private NerellRobotStatus nerellRobotStatus;

    @Autowired
    private NerellServosService nerellServosService;

    @Autowired
    private INerellIOService nerellIOService;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private NerellEcranService ecranService;

    @Autowired
    private RobotGroupService groupService;

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
        // Exclusion du petit port pour l'évittement
        tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(900 + (nerellRobotStatus.team() == ETeam.BLEU ? 0 : 600), 0, 600, 300));

        // Exclusion pour la girouette
        tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(1250, 1900, 500, 100));
    }

    @Override
    public void beforeMatch() {
        positionStrategy();

        // Lancement d'une première lecture de couleurs pour initialiser les capteurs
        nerellIOService.enableLedCapteurCouleur();
        ThreadUtils.sleep(INerellConstantesConfig.WAIT_LED);
        nerellIOService.couleurBoueeAvant1();
        nerellIOService.couleurBoueeAvant2();
        nerellIOService.couleurBoueeAvant3();
        nerellIOService.couleurBoueeAvant4();
        nerellIOService.couleurBoueeArriere2();
        nerellIOService.couleurBoueeArriere4();
        nerellIOService.disableLedCapteurCouleur();

        choixConfig();

        if (nerellRobotStatus.etalonageBaliseOk()) {
            nerellRobotStatus.enableBalise();
        }

        // Visu après la tirette
        nerellIOService.enableLedCapteurCouleur();
        ThreadUtils.sleep(INerellConstantesConfig.WAIT_LED);
        nerellIOService.disableLedCapteurCouleur();
    }

    @Override
    public void startMatch() {
        if (robotStatus.groupOk()) {
            groupService.start();
        }
    }

    @Override
    public void inMatch() {
        // Déclenchement du pavillon
        if (robotStatus.getRemainingTime() <= IEurobotConfig.pavillonRemainingTimeMs
                && !nerellRobotStatus.pavillonSelf() && ioService.auOk()) {
            log.info("Activation du pavillon");
            nerellServosService.pavillonHaut();
            groupService.pavillon();
            nerellRobotStatus.pavillonSelf(true);
        }
    }

    @Override
    public void afterMatch() {
        nerellIOService.releaseAllPompes();
        baliseService.idle();
        nerellRobotStatus.disableBalise();
    }

    @Override
    public void beforePowerOff() {
        nerellServosService.pincesArriereOuvert(false);
        nerellServosService.brasDroitFerme(false);
        nerellServosService.brasGaucheFerme(false);
        nerellServosService.ascenseursAvantHaut(false);
        nerellServosService.ascenseurArriereHaut(false);
        nerellServosService.pivotArriereFerme(false);
        nerellServosService.moustachesFerme(false);
        nerellServosService.pavillonFinMatch();
        ecranService.displayMessage("FIN - Attente béquille et enlever la tirette");
        while (ioService.tirette()) {
            ThreadUtils.sleep(1000);
        }
        nerellIOService.disableAllPompes();
        nerellServosService.pincesArriereFerme(false);
        ThreadUtils.sleep(1000);
    }

    /**
     * Etape du choix de l'équipe/stratégie + config balise
     */
    private void choixEquipeStrategy() {
        ecranService.displayMessage("Choix équipe et lancement calage bordure");

        ChangeFilter<Integer> teamChangeFilter = new ChangeFilter<>(ETeam.UNKNOWN.ordinal());
        ChangeFilter<Integer> strategyChangeFilter = new ChangeFilter<>(null);
        SignalEdgeFilter updatePhotoFilter = new SignalEdgeFilter(false, Type.RISING);
        SignalEdgeFilter doEtalonnageFilter = new SignalEdgeFilter(false, Type.RISING);

        do {
            exitFromScreen();
            connectGroup();
            connectBalise();
            configBalise(updatePhotoFilter, doEtalonnageFilter);

            if (teamChangeFilter.filter(ecranService.config().getTeam())) {
                groupService.team(ETeam.values()[ecranService.config().getTeam()]);
                log.info("Team {}", nerellRobotStatus.team().name());
            }

            if (strategyChangeFilter.filter(ecranService.config().getStrategy())) {
                groupService.strategy(EStrategy.values()[ecranService.config().getStrategy()]);
                log.info("Strategy {}", nerellRobotStatus.strategy().name());
            }

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
                UpdatePhotoInfos query = new UpdatePhotoInfos();
                query.setMessage(photo == null ? "Erreur inconnue" : photo.getErrorMessage());
                query.setPhoto(photo == null ? null : photo.getData());
                ecranService.updatePhoto(query);

            } else if (doEtalonnageFilter.filter(ecranService.config().isEtalonnageBalise())) {
                // sur front montant de "etalonnageBalise" on lance l'étalonnage et envoie le résultat à l'écran
                EtalonnageResponse etalonnage = baliseService.etalonnage();
                UpdatePhotoInfos query = new UpdatePhotoInfos();
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
            if (robotStatus.simulateur() || skip) {
                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    position.setPt(new Point(
                            conv.mmToPulse(INerellConstantesConfig.dstCallage + 150),
                            conv.mmToPulse(2000 - INerellConstantesConfig.dstCallage - 150)
                    ));
                    position.setAngle(conv.degToPulse(-90));
                } else {
                    position.setPt(new Point(
                            conv.mmToPulse(3000 - INerellConstantesConfig.dstCallage - 150),
                            conv.mmToPulse(2000 - INerellConstantesConfig.dstCallage - 150)
                    ));
                    position.setAngle(conv.degToPulse(-90));
                }
            } else {
                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    position.getPt().setX(conv.mmToPulse(INerellConstantesConfig.dstCallage));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.getPt().setX(conv.mmToPulse(3000 - INerellConstantesConfig.dstCallage));
                    position.setAngle(conv.degToPulse(180));
                }

                trajectoryManager.avanceMM(150);
                trajectoryManager.gotoOrientationDeg(-90);

                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                if (!ioService.auOk()) {
                    ecranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
                    throw new ExitProgram(true);
                }

                position.getPt().setY(conv.mmToPulse(2000 - INerellConstantesConfig.dstCallage));
                position.setAngle(conv.degToPulse(-90));

                trajectoryManager.avanceMM(150);
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
        ecranService.displayMessage("Mise en place");
        try {
            trajectoryManager.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            switch (nerellRobotStatus.strategy()) {
                case BASIC:
                    // Aligne vers les bouées au nord du port
//                    if (nerellRobotStatus.team() == ETeam.BLEU) {
//                        trajectoryManager.gotoPoint(220, 1290, GotoOption.ARRIERE);
//                        trajectoryManager.gotoOrientationDeg(66);
//                    } else {
//                        trajectoryManager.gotoPoint(3000 - 220, 1290, GotoOption.ARRIERE);
//                        trajectoryManager.gotoOrientationDeg(180 - 66);
//                    }
//                    break;

                case AGGRESSIVE:
                    // Aligne vers le centre de la table
                    if (nerellRobotStatus.team() == ETeam.BLEU) {
                        trajectoryManager.gotoPoint(270, 1285, GotoOption.AVANT);
                        trajectoryManager.gotoOrientationDeg(-5);
                    } else {
                        trajectoryManager.gotoPoint(3000 - 270, 1285, GotoOption.AVANT);
                        trajectoryManager.gotoOrientationDeg(-175);
                    }
                    break;

                case FINALE:
                    // Aligne vers le centre de la table e faisant le tour
                    if (nerellRobotStatus.team() == ETeam.BLEU) {
                        trajectoryManager.gotoPoint(650, 2000 - 350);
                        trajectoryManager.gotoPoint(650, 1090);
                        if (nerellRobotStatus.groupOk()) {
                            groupService.waitInitStep(1);
                        }
                        trajectoryManager.gotoPoint(245, 1090, GotoOption.ARRIERE);
                        trajectoryManager.alignFrontTo(700, 1270);
                    } else {
                        trajectoryManager.gotoPoint(3000 - 650, 2000 - 350);
                        trajectoryManager.gotoPoint(3000 - 650, 1090);
                        if (nerellRobotStatus.groupOk()) {
                            groupService.waitInitStep(1);
                        }
                        trajectoryManager.gotoPoint(3000 - 245, 1090, GotoOption.ARRIERE);
                        trajectoryManager.alignFrontTo(3000 - 700, 1270);
                    }
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

        while (!ioService.tirette()) {
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
                nerellRobotStatus.deposePartielle(ecranService.config().isDeposePartielle());
                nerellRobotStatus.echangeEcueil(ecranService.config().isEchangeEcueil());

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
