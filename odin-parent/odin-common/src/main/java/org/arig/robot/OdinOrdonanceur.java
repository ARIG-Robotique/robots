package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.constants.OdinConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.OdinEcranService;
import org.arig.robot.services.OdinIOService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

@Slf4j
public class OdinOrdonanceur extends AbstractOrdonanceur {

    @Autowired
    private OdinRobotStatus odinRobotStatus;

    @Autowired
    private OdinIOService odinIO;

    @Autowired
    private RobotGroupService groupService;

    @Autowired
    private OdinEcranService ecranService;

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
    }

    @Override
    public void beforeMatch() {
        positionStrategy();

        // Lancement d'une première lecture de couleurs pour initialiser les capteurs
        odinIO.enableLedCapteurCouleur();
        ThreadUtils.sleep(OdinConstantesConfig.WAIT_LED);
        odinIO.couleurVentouseBas();
        odinIO.couleurVentouseHaut();
        odinIO.disableLedCapteurCouleur();

        choixConfig();

        // Visu après la tirette
        odinIO.enableLedCapteurCouleur();
        ThreadUtils.sleep(OdinConstantesConfig.WAIT_LED);
        odinIO.disableLedCapteurCouleur();
    }

    @Override
    protected boolean waitTirette() {
        return robotStatus.groupOk() ? !groupService.isStart() : io.tirette();
    }

    @Override
    public void startMatch() {
    }

    @Override
    public void inMatch() {
    }

    @Override
    public void afterMatch() {
        odinIO.releaseAllPompes();
    }

    @Override
    public void beforePowerOff() {
        odinIO.disableAllPompes();
        ThreadUtils.sleep(1000);
    }

    /**
     * Etape du choix de l'équipe/stratégie
     */
    private void choixEquipeStrategy() {
        ChangeFilter<Team> teamChangeFilter = new ChangeFilter<>(null);
        ChangeFilter<Strategy> strategyChangeFilter = new ChangeFilter<>(null);
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
                    odinRobotStatus.setTeam(ecranService.config().getTeam());
                    log.info("Team {}", odinRobotStatus.team().name());
                }

                if (strategyChangeFilter.filter(ecranService.config().getStrategy())) {
                    odinRobotStatus.strategy(ecranService.config().getStrategy());
                    log.info("Strategy {}", odinRobotStatus.strategy().name());
                }

                odinRobotStatus.statuettePresente(ecranService.config().hasOption(EurobotConfig.STATUETTE_PRESENTE));
                odinRobotStatus.vitrinePresente(ecranService.config().hasOption(EurobotConfig.VITRINE_PRESENTE));

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
                if (odinRobotStatus.team() == Team.JAUNE) {
                    position.setPt(new Point(
                            conv.mmToPulse(OdinConstantesConfig.dstCallage + 150),
                            conv.mmToPulse(OdinConstantesConfig.dstCallage + 150)
                    ));
                    position.setAngle(conv.degToPulse(90));
                } else {
                    position.setPt(new Point(
                            conv.mmToPulse(3000 - OdinConstantesConfig.dstCallage - 150),
                            conv.mmToPulse(OdinConstantesConfig.dstCallage + 150)
                    ));
                    position.setAngle(conv.degToPulse(90));
                }
            } else {
                robotStatus.enableCalageBordure();
                mv.reculeMMSansAngle(1000);

                if (odinRobotStatus.team() == Team.JAUNE) {
                    position.getPt().setX(conv.mmToPulse(OdinConstantesConfig.dstCallage));
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.getPt().setX(conv.mmToPulse(3000 - OdinConstantesConfig.dstCallage));
                    position.setAngle(conv.degToPulse(180));
                }

                mv.avanceMM(150);
                mv.gotoOrientationDeg(90);

                robotStatus.enableCalageBordure();
                mv.reculeMMSansAngle(1000);

                if (!io.auOk()) {
                    ecranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
                    throw new ExitProgram(true);
                }

                position.getPt().setY(conv.mmToPulse(OdinConstantesConfig.dstCallage));
                position.setAngle(conv.degToPulse(90));

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
        ecranService.displayMessage("Mise en place");
        try {
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            switch (odinRobotStatus.strategy()) {
                case BASIC:
                case AGGRESSIVE:
                    // Aligne vers les bouées au nord du port
                    if (odinRobotStatus.team() == Team.JAUNE) {
                        mv.gotoPoint(255, 1005, GotoOption.ARRIERE);
                    } else {
                        mv.gotoPoint(3000 - 255, 1005, GotoOption.ARRIERE);
                    }
                    mv.gotoOrientationDeg(-90);
                    break;

                case FINALE:
                    // Aligne vers le centre de la table
                    if (odinRobotStatus.team() == Team.JAUNE) {
                        mv.gotoPoint(160, 1370, GotoOption.AVANT);
                        mv.gotoOrientationDeg(-70);
                    } else {
                        mv.gotoPoint(3000 - 160, 1370, GotoOption.AVANT);
                        mv.gotoOrientationDeg(-110);
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
