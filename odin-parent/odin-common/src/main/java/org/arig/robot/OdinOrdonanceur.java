package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.EOdinStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

@Slf4j
public class OdinOrdonanceur extends AbstractOrdonanceur {

    @Autowired
    private OdinRobotStatus odinRobotStatus;

    @Override
    public String getPathfinderMap() {
        return null;
    }

    @Override
    public void afterInit() {
        choixEquipe();
    }

    @Override
    public void beforeMatch() {
        choixStrategie();
    }

    @Override
    public void inMatch() {
    }

    @Override
    public void afterMatch() {
    }

    @Override
    public void beforePowerOff() {
        ThreadUtils.sleep(1000);
    }

    /**
     * Etape du choix de l'équipe + config balise
     */
    private void choixEquipe() {
        ecranService.displayMessage("Choix équipe et lancement calage bordure");
        ChangeFilter<Integer> teamChangeFilter = new ChangeFilter<>(-1);
        do {
            exitFromScreen();
            if (teamChangeFilter.filter(ecranService.config().getTeam())) {
                odinRobotStatus.setTeam(ecranService.config().getTeam());
                log.info("Team {}", odinRobotStatus.team().name());
            }
            ThreadUtils.sleep(500);
        } while (!ecranService.config().isStartCalibration());
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
                    position.setPt(new Point(conv.mmToPulse(200), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setPt(new Point(conv.mmToPulse(3000 - 200), conv.mmToPulse(1200)));
                    position.setAngle(conv.degToPulse(180));
                }
            } else {
                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                if (odinRobotStatus.team() == ETeam.BLEU) {
                    position.getPt().setX(conv.mmToPulse(IConstantesOdinConfig.dstCallageY));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.getPt().setX(conv.mmToPulse(3000 - IConstantesOdinConfig.dstCallageY));
                    position.setAngle(conv.degToPulse(180));
                }

                trajectoryManager.avanceMM(150);
                trajectoryManager.gotoOrientationDeg(-90);

                robotStatus.enableCalageBordure();
                trajectoryManager.reculeMMSansAngle(1000);

                position.getPt().setY(conv.mmToPulse(2000 - IConstantesOdinConfig.dstCallageY));
                position.setAngle(conv.degToPulse(-90));

                trajectoryManager.avanceMM(150);

                if (odinRobotStatus.team() == ETeam.BLEU) {
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
            if (odinRobotStatus.strategy() == EOdinStrategy.AGGRESSIVE) {
                if (odinRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                    trajectoryManager.alignFrontTo(1025, 1400);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                    trajectoryManager.alignFrontTo(3000 - 1025, 1400);
                }
            } else if (odinRobotStatus.strategy() == EOdinStrategy.FINALE) {
                if (odinRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                } else {
                    trajectoryManager.gotoPoint(3000 - 200, 1200);
                    trajectoryManager.gotoOrientationDeg(0);
                }
            } else if (odinRobotStatus.strategy() == EOdinStrategy.BASIC_NORD) { // BASIC
                // Aligne vers les bouées au nord du port
                if (odinRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(220, 1290);
                    trajectoryManager.gotoOrientationDeg(66);
                } else {
                    trajectoryManager.gotoPoint(3000 - 220, 1290);
                    trajectoryManager.gotoOrientationDeg(180 - 66);
                }
            } else if (odinRobotStatus.strategy() == EOdinStrategy.BASIC_SUD) {
                // Aligne vers les bouées au sud du port
                if (odinRobotStatus.team() == ETeam.BLEU) {
                    trajectoryManager.gotoPoint(220, 1110);
                    trajectoryManager.gotoOrientationDeg(-66);
                } else {
                    trajectoryManager.gotoPoint(3000 - 220, 1110);
                    trajectoryManager.gotoOrientationDeg(-180 + 66);
                }
            } else { // Au centre orienté vers le logo au centre de la table
                if (odinRobotStatus.team() == ETeam.BLEU) {
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
                odinRobotStatus.doubleDepose(ecranService.config().isDoubleDepose() || ecranService.config().isDeposePartielle());
                odinRobotStatus.deposePartielle(ecranService.config().isDeposePartielle());
                if (strategyChangeFilter.filter(ecranService.config().getStrategy())) {
                    odinRobotStatus.setStrategy(ecranService.config().getStrategy());
                    log.info("Strategy {}", odinRobotStatus.strategy().name());
                    positionStrategy();
                }
            }

            avoidingService.setSafeAvoidance(ecranService.config().isSafeAvoidance());

            ThreadUtils.sleep(manuel ? 4000 : 500);
        }
    }
}
