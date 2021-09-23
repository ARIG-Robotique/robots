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
        choixEquipeStrategy();
    }

    @Override
    public void addDeadZones() {
        // Exclusion du petit port pour l'évittement
        tableUtils.addPersistentDeadZone(new java.awt.Rectangle.Double(900 + (odinRobotStatus.team() == ETeam.BLEU ? 0 : 600), 0, 600, 300));
    }

    @Override
    public void beforeMatch() {
        positionStrategy();

        // Lancement d'une première lecture de couleurs pour initialiser les capteurs
        odinIOService.enableLedCapteurCouleur();
        ThreadUtils.sleep(IOdinConstantesConfig.WAIT_LED);
        odinIOService.couleurBoueeAvantGauche();
        odinIOService.couleurBoueeAvantDroit();
        odinIOService.couleurBoueeArriereGauche();
        odinIOService.couleurBoueeArriereDroit();
        odinIOService.disableLedCapteurCouleur();

        choixConfig();

        // Visu après la tirette
        odinIOService.enableLedCapteurCouleur();
        ThreadUtils.sleep(IOdinConstantesConfig.WAIT_LED);
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
     * Etape du choix de l'équipe/stratégie
     */
    private void choixEquipeStrategy() {
        ChangeFilter<Integer> teamChangeFilter = new ChangeFilter<>(ETeam.UNKNOWN.ordinal());
        ChangeFilter<Integer> strategyChangeFilter = new ChangeFilter<>(null);
        ChangeFilter<Boolean> groupChangeFilter = new ChangeFilter<>(null);

        boolean done;
        do {
            exitFromScreen();
            connectGroup();

            if (groupChangeFilter.filter(robotStatus.groupOk())) {
                if (robotStatus.groupOk()) {
                    ecranService.displayMessage("Attente configuration Nerell");
                } else {
                    ecranService.displayMessage("Choix équipe et lancement calage bordure");
                }
            }

            if (robotStatus.groupOk()) {
                done = groupService.isCalage();

            } else {
                if (teamChangeFilter.filter(ecranService.config().getTeam())) {
                    groupService.team(ETeam.values()[ecranService.config().getTeam()]);
                    log.info("Team {}", odinRobotStatus.team().name());
                }

                if (strategyChangeFilter.filter(ecranService.config().getStrategy())) {
                    groupService.strategy(EStrategy.values()[ecranService.config().getStrategy()]);
                    log.info("Strategy {}", odinRobotStatus.strategy().name());
                }

                done = ecranService.config().isStartCalibration();
            }

            ThreadUtils.sleep(200);
        } while (!done);
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
                    position.setPt(new Point(
                            conv.mmToPulse(IOdinConstantesConfig.dstCallage + 150),
                            conv.mmToPulse(IOdinConstantesConfig.dstCallage + 150)
                    ));
                    position.setAngle(conv.degToPulse(90));
                } else {
                    position.setPt(new Point(
                            conv.mmToPulse(3000 - IOdinConstantesConfig.dstCallage - 150),
                            conv.mmToPulse(IOdinConstantesConfig.dstCallage + 150)
                    ));
                    position.setAngle(conv.degToPulse(90));
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

            switch (odinRobotStatus.strategy()) {
                case BASIC:
                case AGGRESSIVE:
                    // Aligne vers les bouées au nord du port
                    if (odinRobotStatus.team() == ETeam.BLEU) {
                        trajectoryManager.gotoPoint(255, 1005, GotoOption.ARRIERE);
                    } else {
                        trajectoryManager.gotoPoint(3000 - 255, 1005, GotoOption.ARRIERE);
                    }
                    trajectoryManager.gotoOrientationDeg(-90);
                    break;

                case FINALE:
                    // Aligne vers le centre de la table
                    if (odinRobotStatus.team() == ETeam.BLEU) {
                        trajectoryManager.gotoPoint(160, 1370, GotoOption.AVANT);
                        trajectoryManager.gotoOrientationDeg(-70);
                    } else {
                        trajectoryManager.gotoPoint(3000 - 160, 1370, GotoOption.AVANT);
                        trajectoryManager.gotoOrientationDeg(-110);
                    }
                    groupService.initStep(1);
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
        if (robotStatus.groupOk()) {
            ecranService.displayMessage("Attente démarrage Nerell");

            while (!groupService.isReady()) {
                exitFromScreen();
                robotStatus.twoRobots(true);
                avoidingService.setSafeAvoidance(ecranService.config().isSafeAvoidance());
                ThreadUtils.sleep(200);
            }

        } else {
            ecranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");

            SignalEdgeFilter manuelRisingEdge = new SignalEdgeFilter(ecranService.config().isModeManuel(), Type.RISING);
            SignalEdgeFilter manuelFallingEdge = new SignalEdgeFilter(ecranService.config().isModeManuel(), Type.FALLING);

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

                // Si on est pas en manuel, gestion de la config
                if (!manuel && !ecranService.config().isSkipCalageBordure()) {
                    ecranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");
                }

                robotStatus.twoRobots(ecranService.config().isTwoRobots());
                avoidingService.setSafeAvoidance(ecranService.config().isSafeAvoidance());

                ThreadUtils.sleep(manuel ? 4000 : 200);
            }
        }
    }
}
