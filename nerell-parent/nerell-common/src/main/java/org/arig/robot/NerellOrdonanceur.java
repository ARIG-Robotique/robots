package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.ENerellStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.ecran.UpdatePhotoInfos;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.services.NerellServosService;
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
    private BaliseService baliseService;

    @Autowired
    private NerellEcranService ecranService;

    @Override
    public String getPathfinderMap() {
        return nerellRobotStatus.team().pathfinderMap();
    }

    @Override
    public void afterInit() {
        choixEquipe();
    }

    @Override
    public void beforeMatch() {
        choixStrategie();

        nerellRobotStatus.enableBalise();
        nerellRobotStatus.enablePincesAvant();
    }

    @Override
    public void inMatch() {
        // Déclenchement du pavillon
        if (robotStatus.getRemainingTime() <= IEurobotConfig.pavillonRemainingTimeMs
                && !nerellRobotStatus.pavillon() && ioService.auOk()) {
            log.info("Activation du pavillon");
            nerellServosService.pavillonHaut();
            nerellRobotStatus.pavillon(true);
        }
    }

    @Override
    public void afterMatch() {
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
        nerellServosService.pincesArriereFerme(false);
        ThreadUtils.sleep(1000);
    }

    /**
     * Etape du choix de l'équipe + config balise
     */
    private void choixEquipe() {
        ecranService.displayMessage("Choix équipe et lancement calage bordure");

        ChangeFilter<Integer> teamChangeFilter = new ChangeFilter<>(-1);
        SignalEdgeFilter updatePhotoFilter = new SignalEdgeFilter(false, Type.RISING);
        SignalEdgeFilter doEtalonnageFilter = new SignalEdgeFilter(false, Type.RISING);

        do {
            exitFromScreen();

            if (teamChangeFilter.filter(ecranService.config().getTeam())) {
                nerellRobotStatus.setTeam(ecranService.config().getTeam());
                log.info("Team {}", nerellRobotStatus.team().name());
            }

            connectGroup();
            connectBalise();
            configBalise(updatePhotoFilter, doEtalonnageFilter);

            ThreadUtils.sleep(500);
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
        }
    }

    /**
     * Calage sur la bordure
     */
    @Override
    public void calageBordure(boolean skip) {
        ecranService.displayMessage("Calage bordure");

        try {
            robotStatus.disableAvoidance();
            if (robotStatus.simulateur() || skip) {
                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    position.setPt(new Point(conv.mmToPulse(200), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setPt(new Point(conv.mmToPulse(3000 - 200), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(180));
                }
            } else {
                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    position.getPt().setX(conv.mmToPulse(IConstantesNerellConfig.dstCallageY));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.getPt().setX(conv.mmToPulse(3000 - IConstantesNerellConfig.dstCallageY));
                    position.setAngle(conv.degToPulse(180));
                }

                trajectoryManager.avanceMM(150);
                trajectoryManager.gotoOrientationDeg(-90);

                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                position.getPt().setY(conv.mmToPulse(2000 - IConstantesNerellConfig.dstCallageY));
                position.setAngle(conv.degToPulse(-90));

                trajectoryManager.avanceMM(150);

                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                }
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
        try {
            if (nerellRobotStatus.strategy() == ENerellStrategy.AGGRESSIVE) {
                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                    trajectoryManager.alignFrontTo(1025, 1400);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                    trajectoryManager.alignFrontTo(3000 - 1025, 1400);
                }
            } else if (nerellRobotStatus.strategy() == ENerellStrategy.FINALE) {
                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                }
            } else if (nerellRobotStatus.strategy() == ENerellStrategy.BASIC_NORD) { // BASIC
                // Aligne vers les bouées au nord du port
                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(220, 1290);
                    trajectoryManager.gotoOrientationDeg(66);
                } else {
                    trajectoryManager.gotoPoint(3000 - 220, 1290);
                    trajectoryManager.gotoOrientationDeg(180 - 66);
                }
            } else if (nerellRobotStatus.strategy() == ENerellStrategy.BASIC_SUD) {
                // Aligne vers les bouées au sud du port
                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(220, 1110);
                    trajectoryManager.gotoOrientationDeg(-66);
                } else {
                    trajectoryManager.gotoPoint(3000 - 220, 1110);
                    trajectoryManager.gotoOrientationDeg(-180 + 66);
                }
            } else { // Au centre orienté vers le logo au centre de la table
                if (nerellRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                }
            }

        } catch (AvoidingException e) {
            ecranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
        }
    }

    /**
     * Etape du choix des options + config balise
     */
    private void choixStrategie() {
        ecranService.displayMessage("Attente mise de la tirette, choix strategie ou mode manuel");

        SignalEdgeFilter manuelRisingEdge = new SignalEdgeFilter(ecranService.config().isModeManuel(), Type.RISING);
        SignalEdgeFilter manuelFallingEdge = new SignalEdgeFilter(ecranService.config().isModeManuel(), Type.FALLING);
        ChangeFilter<Integer> strategyChangeFilter = new ChangeFilter<>(-1);
        SignalEdgeFilter updatePhotoFilter = new SignalEdgeFilter(false, Type.RISING);
        SignalEdgeFilter doEtalonnageFilter = new SignalEdgeFilter(false, Type.RISING);

        boolean manuel = ecranService.config().isModeManuel();

        while (!ioService.tirette()) {
            exitFromScreen();

            if (manuelRisingEdge.filter(ecranService.config().isModeManuel())) {
                manuel = true;
                strategyChangeFilter.filter(-1);
                ecranService.displayMessage("!!!! Mode manuel !!!!");
                startMonitoring();
            } else if (manuel && manuelFallingEdge.filter(ecranService.config().isModeManuel())) {
                manuel = false;
                endMonitoring();
                ecranService.displayMessage("Attente mise de la tirette, choix strategie ou mode manuel");
            }

            // Si on est pas en manuel, gestion de la strategy
            if (!manuel && !ecranService.config().isSkipCalageBordure()) {
                ecranService.displayMessage("Attente mise de la tirette, choix strategie ou mode manuel");
                nerellRobotStatus.doubleDepose(ecranService.config().isDoubleDepose() || ecranService.config().isDeposePartielle());
                nerellRobotStatus.deposePartielle(ecranService.config().isDeposePartielle());
                if (strategyChangeFilter.filter(ecranService.config().getStrategy())) {
                    nerellRobotStatus.setStrategy(ecranService.config().getStrategy());
                    log.info("Strategy {}", nerellRobotStatus.strategy().name());
                    positionStrategy();
                }
            }

            avoidingService.setSafeAvoidance(ecranService.config().isSafeAvoidance());

            connectGroup();
            connectBalise();
            configBalise(updatePhotoFilter, doEtalonnageFilter);

            ThreadUtils.sleep(manuel ? 4000 : 500);
        }
    }
}
