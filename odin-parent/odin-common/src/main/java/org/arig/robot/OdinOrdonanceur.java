package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.IOdinConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.services.OdinServosService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

@Slf4j
public class OdinOrdonanceur extends AbstractOrdonanceur {

    @Autowired
    private OdinRobotStatus odinRobotStatus;

    @Autowired
    private OdinServosService odinServosService;

    @Autowired
    private RobotGroupService groupService;

    @Autowired
    private IOdinIOService odinIOService;

    @Override
    public String getPathfinderMap() {
        return odinRobotStatus.team().pathfinderMap("odin");
    }

    @Override
    public void afterInit() {
        choixEquipe();
    }

    @Override
    public void beforeMatch() {
        choixStrategie();

        // Optimisation d'activation des pinces
        odinRobotStatus.enablePincesAvant();
        odinRobotStatus.enablePincesArriere();

        // Lancement d'une première lecture de couleurs pour initialiser les capteurs
        odinIOService.enableLedCapteurCouleur();
        ThreadUtils.sleep(IOdinConstantesConfig.WAIT_LED);
        odinIOService.couleurBoueeAvantGauche();
        odinIOService.couleurBoueeAvantDroit();
        odinIOService.couleurBoueeArriereGauche();
        odinIOService.couleurBoueeArriereDroit();
        odinIOService.disableLedCapteurCouleur();
    }

    @Override
    protected boolean waitTirette() {
        return robotStatus.groupOk() ? !groupService.isStart() : ioService.tirette();
    }

    @Override
    public void startMatch() {

    }

    @Override
    public void inMatch() {
        // Déclenchement du pavillon
        if (robotStatus.getRemainingTime() <= IEurobotConfig.pavillonRemainingTimeMs
                && !odinRobotStatus.pavillonSelf() && ioService.auOk()) {
            log.info("Activation du pavillon");
            odinServosService.pavillonHaut();
            groupService.pavillon();
            odinRobotStatus.pavillonSelf(true);
        }
    }

    @Override
    public void afterMatch() {
        odinIOService.releaseAllPompe();
    }

    @Override
    public void beforePowerOff() {
        odinServosService.brasDroitFerme(false);
        odinServosService.brasGaucheFerme(false);
        odinServosService.poussoirsArriereBas(false);
        odinServosService.poussoirsAvantBas(false);
        odinServosService.pavillonFinMatch();
        odinIOService.disableAllPompe();
        ThreadUtils.sleep(1000);
    }

    /**
     * Etape du choix de l'équipe
     */
    private void choixEquipe() {
        ecranService.displayMessage("Choix équipe et lancement calage bordure");
        ChangeFilter<Integer> teamChangeFilter = new ChangeFilter<>(ETeam.UNKNOWN.ordinal());
        do {
            exitFromScreen();
            connectGroup();

            if (teamChangeFilter.filter(ecranService.config().getTeam())) {
                odinRobotStatus.setTeam(ETeam.values()[ecranService.config().getTeam()]);
                log.info("Team {}", odinRobotStatus.team().name());
            }
            ThreadUtils.sleep(200);
        } while (!ecranService.config().isStartCalibration() && !groupService.isCalage());
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
                if (odinRobotStatus.team() == ETeam.BLEU) {
                    position.setPt(new Point(conv.mmToPulse(IOdinConstantesConfig.dstCallage), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setPt(new Point(conv.mmToPulse(3000 - IOdinConstantesConfig.dstCallage), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(180));
                }
            } else {
                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                if (odinRobotStatus.team() == ETeam.BLEU) {
                    position.getPt().setX(conv.mmToPulse(IOdinConstantesConfig.dstCallage));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.getPt().setX(conv.mmToPulse(3000 - IOdinConstantesConfig.dstCallage));
                    position.setAngle(conv.degToPulse(180));
                }

                trajectoryManager.avanceMM(150);
                trajectoryManager.gotoOrientationDeg(90);

                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                if (!ioService.auOk()) {
                    ecranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
                    throw new ExitProgram(true);
                }

                position.getPt().setY(conv.mmToPulse(IOdinConstantesConfig.dstCallage));
                position.setAngle(conv.degToPulse(90));

                trajectoryManager.avanceMM(150);

                trajectoryManager.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                if (odinRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(255, 900, GotoOption.ARRIERE);
                } else {
                    trajectoryManager.gotoPoint(3000 - 255, 900, GotoOption.ARRIERE);
                }
            }
        } catch (AvoidingException e) {
            ecranService.displayMessage("Erreur lors du calage bordure", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer pour le départ", e);
        }
    }

    /**
     * Positionnement en fonction de la stratégie
     * @param oldStrat
     */
    public void positionStrategy(EStrategy oldStrat) {
        ecranService.displayMessage("Mise en place");
        try {
            EStrategy newStrat = odinRobotStatus.strategy();
            if (oldStrat != newStrat) {
                if (odinRobotStatus.team() == ETeam.BLEU) {
                    if (oldStrat == EStrategy.BASIC_NORD) {
                        trajectoryManager.gotoPoint(255, 900);
                        if (newStrat == EStrategy.BASIC_SUD) {
                            trajectoryManager.gotoPoint(580, 900);
                            trajectoryManager.gotoPoint(580, 1500);
                            trajectoryManager.gotoPoint(255, 1500);
                        } else if (newStrat == EStrategy.AGGRESSIVE) {
                            // TODO
                        }
                    } else if (oldStrat == EStrategy.BASIC_SUD) {
                        trajectoryManager.gotoPoint(255, 1500);
                        if (newStrat == EStrategy.BASIC_NORD) {
                            trajectoryManager.gotoPoint(580, 1500);
                            trajectoryManager.gotoPoint(580, 900);
                            trajectoryManager.gotoPoint(255, 900);
                        } else if (newStrat == EStrategy.AGGRESSIVE) {
                            // TODO
                        }
                    } else if (oldStrat == EStrategy.AGGRESSIVE) {
                        // TODO
                    }
                } else {
                    // TODO
                }
            }

            if (newStrat == EStrategy.AGGRESSIVE) {
                // TODO
            } else if (newStrat == EStrategy.BASIC_NORD) { // BASIC
                // Aligne vers les bouées au nord du port
                if (odinRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(255, 1005, GotoOption.ARRIERE);
                } else {
                    trajectoryManager.gotoPoint(3000 - 255, 1005, GotoOption.ARRIERE);
                }
                trajectoryManager.gotoOrientationDeg(-90);
            } else if (newStrat == EStrategy.BASIC_SUD) {
                // Aligne vers les bouées au sud du port
                if (odinRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(255, 1395, GotoOption.ARRIERE);
                } else {
                    trajectoryManager.gotoPoint(3000 - 255, 1395, GotoOption.ARRIERE);
                }
                trajectoryManager.gotoOrientationDeg(90);
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
        ChangeFilter<EStrategy> strategyChangeFilter = new ChangeFilter<>(null);

        boolean manuel = ecranService.config().isModeManuel();
        EStrategy oldStrat = odinRobotStatus.strategy();

        while (robotStatus.groupOk() ? !groupService.isReady() : !ioService.tirette()) {
            exitFromScreen();

            if (manuelRisingEdge.filter(ecranService.config().isModeManuel())) {
                manuel = true;
                strategyChangeFilter.filter(null);
                oldStrat = null;
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

                if (!robotStatus.groupOk()) {
                    odinRobotStatus.doubleDepose(ecranService.config().isDoubleDepose() || ecranService.config().isDeposePartielle());
                    odinRobotStatus.deposePartielle(ecranService.config().isDeposePartielle());
                    odinRobotStatus.echangeEcueil(ecranService.config().isEchangeEcueil());
                    odinRobotStatus.strategy(EStrategy.values()[ecranService.config().getStrategy()]);
                }

                if (strategyChangeFilter.filter(odinRobotStatus.strategy())) {
                    log.info("Strategy {}", odinRobotStatus.strategy().name());
                    positionStrategy(oldStrat);
                    oldStrat = odinRobotStatus.strategy();
                }
            }

            robotStatus.twoRobots(ecranService.config().isTwoRobots());
            avoidingService.setSafeAvoidance(ecranService.config().isSafeAvoidance());

            connectGroup();

            ThreadUtils.sleep(manuel ? 4000 : 200);
        }
    }
}
