package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.constants.OdinConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.InitStep;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.SiteDeRetour;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.ecran.EcranPhoto;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.OdinEcranService;
import org.arig.robot.services.OdinIOService;
import org.arig.robot.services.OdinServosService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.system.capteurs.CarreFouilleReader;
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
    private BaliseService baliseService;

    @Autowired
    private OdinEcranService odinEcranService;

    @Autowired
    private BrasService brasService;

    @Autowired
    private OdinServosService odinServosService;

    @Autowired
    private CarreFouilleReader carreFouilleReader;

    private int getX(int x) {
        return tableUtils.getX(odinRobotStatus.team() == Team.VIOLET, x);
    }

    private int getX(double x) {
        return getX((int) x);
    }

    @Override
    public String getPathfinderMap() {
        return odinRobotStatus.team().pathfinderMap("odin");
    }

    @Override
    public void afterInit() {
        try {
            carreFouilleReader.printStateStock(null, null, null, null, null, null);
            carreFouilleReader.printStateVentouse(null, null);
        } catch (Exception e) {
            // NOPE
        }
        choixEquipeStrategy();
    }

    @Override
    public void addDeadZones() {
        // campement
//        if (odinRobotStatus.team() == Team.JAUNE) {
//            tableUtils.addPersistentDeadZone(new Rectangle2D.Double(0, 1000, 400, 600));
//        } else {
//            tableUtils.addPersistentDeadZone(new Rectangle2D.Double(2600, 1000, 400, 600));
//        }
    }

    @Override
    public void beforeMatch(boolean skip) {
        if (!skip) {
            positionStrategy();
        }

        // Lancement d'une première lecture de couleurs pour initialiser les capteurs
        odinIO.enableLedCapteurCouleur();
        ThreadUtils.sleep(OdinConstantesConfig.WAIT_LED);
        odinIO.couleurVentouseBas();
        odinIO.couleurVentouseHaut();
        odinIO.disableLedCapteurCouleur();

        choixConfig();

        if (!skip && !robotStatus.twoRobots()) {
            try {
                ThreadUtils.sleep(2000);
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation(20));
                mv.tourneDeg(180);

            } catch (AvoidingException e) {
                odinEcranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
                throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
            }
        }

        if (odinRobotStatus.etalonageBaliseOk()) {
            odinRobotStatus.enableBalise();
        }

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
        // Nope
    }

    @Override
    public void inMatch() {
        // Nope
    }

    @Override
    public void afterMatch() {
        double currentX = mv.currentXMm();
        double currentY = mv.currentYMm();
        if ((currentX <= 500 || currentX >= 2500) && currentY <= 1700 && currentY >= 900) {
            groupService.siteDeRetour(currentY > 1300 ? SiteDeRetour.CAMPEMENT_NORD : SiteDeRetour.CAMPEMENT_SUD);
        }

        odinIO.releaseAllPompes();
        if (!robotStatus.twoRobots()) {
            baliseService.idle();
        }
        odinRobotStatus.disableBalise();
    }

    @Override
    public void beforePowerOff() {
        odinIO.disableAllPompes();
        odinIO.enableAlimServos();

        odinEcranService.displayMessage("FIN - Enlever la tirette quand stock vide.");

        brasService.setBrasHaut(PositionBras.HORIZONTAL);
        brasService.setBrasBas(PositionBras.HORIZONTAL);

        if (!odinRobotStatus.repliqueDepose()) {
            odinServosService.langueOuvert(true);
            odinServosService.pousseRepliquePoussette(false);
        }

        while (io.tirette()) {
            ThreadUtils.sleep(1000);
        }

        try {
            odinRobotStatus.ventouseBas(null);
            odinRobotStatus.ventouseHaut(null);
            while (odinRobotStatus.stockTaille() > 0) {
                odinRobotStatus.destockage();
            }
            carreFouilleReader.printStateVentouse(null, null);
            carreFouilleReader.printStateStock(null, null, null, null, null, null);
        } catch (Exception e) {
            // NOPE
        }

        brasService.setBrasBas(PositionBras.INIT);
        brasService.setBrasHaut(PositionBras.INIT);
        odinServosService.homes();
    }

    /**
     * Etape du choix de l'équipe/stratégie
     */
    private void choixEquipeStrategy() {
        ChangeFilter<Team> teamChangeFilter = new ChangeFilter<>(null);
        ChangeFilter<Strategy> strategyChangeFilter = new ChangeFilter<>(null);
        ChangeFilter<Boolean> groupChangeFilter = new ChangeFilter<>(null);
        SignalEdgeFilter updatePhotoFilter = new SignalEdgeFilter(false, Type.RISING);
        SignalEdgeFilter doEtalonnageFilter = new SignalEdgeFilter(false, Type.RISING);

        boolean done;
        do {
            exitFromScreen();
            connectBalise();
            connectGroup();
            if (!robotStatus.twoRobots()) {
                configBalise(updatePhotoFilter, doEtalonnageFilter);
            }

            if (Boolean.TRUE.equals(groupChangeFilter.filter(robotStatus.groupOk()))) {
                if (robotStatus.groupOk()) {
                    odinEcranService.displayMessage("Attente configuration Nerell");
                } else {
                    odinEcranService.displayMessage("Choix équipe et lancement calage bordure");
                }
            }

            if (robotStatus.groupOk()) {
                done = groupService.isCalage();

            } else {
                if (Boolean.TRUE.equals(teamChangeFilter.filter(odinEcranService.config().getTeam()))) {
                    odinRobotStatus.setTeam(odinEcranService.config().getTeam());
                    log.info("Team {}", odinRobotStatus.team().name());
                }

                if (Boolean.TRUE.equals(strategyChangeFilter.filter(odinEcranService.config().getStrategy()))) {
                    odinRobotStatus.strategy(odinEcranService.config().getStrategy());
                    log.info("Strategy {}", odinRobotStatus.strategy().name());
                }

                odinRobotStatus.twoRobots(odinEcranService.config().isTwoRobots());
                odinRobotStatus.doubleDeposeGalerie(odinEcranService.config().hasOption(EurobotConfig.DOUBLE_DEPOSE_GALERIE));

                done = odinEcranService.config().isStartCalibration();
            }

            ThreadUtils.sleep(200);
        } while (!done);
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
            if (Boolean.TRUE.equals(updatePhotoFilter.filter(odinEcranService.config().isUpdatePhoto()))) {
                // sur front montant de "updatePhoto" on prend une photo et l'envoie à l'écran
                PhotoResponse photo = baliseService.getPhoto();
                EcranPhoto query = new EcranPhoto();
                query.setMessage(photo == null ? "Erreur inconnue" : photo.getErrorMessage());
                query.setPhoto(photo == null ? null : photo.getData());
                odinEcranService.updatePhoto(query);

            } else if (Boolean.TRUE.equals(doEtalonnageFilter.filter(odinEcranService.config().isEtalonnageBalise()))) {
                // sur front montant de "etalonnageBalise" on lance l'étalonnage et envoie le résultat à l'écran
                EtalonnageResponse etalonnage = baliseService.etalonnage();
                EcranPhoto query = new EcranPhoto();
                query.setMessage(etalonnage == null ? "Erreur inconnue" : etalonnage.getErrorMessage());
                query.setPhoto(etalonnage == null ? null : etalonnage.getData());
                odinEcranService.updatePhoto(query);
            }

            odinRobotStatus.etalonageBaliseOk(odinEcranService.config().isEtalonnageOk());
        } else {
            odinRobotStatus.etalonageBaliseOk(false);
        }
    }

    /**
     * Calage sur la bordure
     */
    @Override
    public void calageBordure(boolean skip) {
        odinEcranService.displayMessage("Calage bordure");

        try {
            robotStatus.disableAvoidance();
            position.setPt(new Point(
                    conv.mmToPulse(getX(OdinConstantesConfig.dstCallage)),
                    conv.mmToPulse(1785)
            ));
            if (odinRobotStatus.team() == Team.JAUNE) {
                position.setAngle(conv.degToPulse(0));
            } else {
                position.setAngle(conv.degToPulse(180));
            }
            if (!skip) {
                robotStatus.enableCalageBordure(TypeCalage.ARRIERE);
                mv.reculeMMSansAngle(300);

                position.getPt().setX(conv.mmToPulse(getX(OdinConstantesConfig.dstCallage)));
                if (odinRobotStatus.team() == Team.JAUNE) {
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setAngle(conv.degToPulse(180));
                }

                mv.avanceMM(70);
                mv.gotoOrientationDeg(90);

                robotStatus.enableCalageBordure(TypeCalage.AVANT_BAS);
                mv.avanceMM(400);
                robotStatus.enableCalageBordure(TypeCalage.AVANT_BAS);
                mv.avanceMMSansAngle(100);

                if (!io.auOk()) {
                    odinEcranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
                    throw new ExitProgram(true);
                }

                position.getPt().setY(conv.mmToPulse(EurobotConfig.tableHeight - OdinConstantesConfig.dstCallage));
                position.setAngle(conv.degToPulse(90));

                mv.reculeMM(70);
            }
        } catch (AvoidingException e) {
            odinEcranService.displayMessage("Erreur lors du calage bordure", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer pour le départ", e);
        }
    }

    /**
     * Positionnement en fonction de la stratégie
     */
    public void positionStrategy() {
        odinEcranService.displayMessage("Mise en place");

        try {
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            GotoOption sensFinal = GotoOption.ARRIERE;
            switch (odinRobotStatus.strategy()) {
                case FINALE_1:
                    sensFinal = GotoOption.AVANT;
                case FINALE_2:
                    if (!robotStatus.twoRobots()) {
                        mv.gotoPoint(getX(265), 1430);
                        mv.alignFrontTo(getX(750), 1550);
                        break;
                    } else {
                        mv.gotoPoint(getX(240), 1740);
                        mv.gotoPoint(getX(570), 1740);
                        groupService.initStep(InitStep.ODIN_DEVANT_GALERIE); // Odin calé, en attente devant la galerie
                        mv.gotoPoint(getX(570), 1160);
                        mv.gotoOrientationDeg(odinRobotStatus.team() == Team.JAUNE ? 0 : 180);
                        odinEcranService.displayMessage("Attente calage Nerell");
                        groupService.waitInitStep(InitStep.NERELL_CALAGE_TERMINE); // Attente Nerell calé
                        mv.gotoPoint(getX(robotConfig.distanceCalageArriere() + 20), 1160, sensFinal);
                        mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
                        robotStatus.enableCalageBordure(TypeCalage.ARRIERE);
                        mv.reculeMMSansAngle(30);
                    }
                    groupService.initStep(InitStep.ODIN_EN_POSITION);
                    break;

                case BASIC:
                default:
                    if (robotStatus.twoRobots()) {
                        mv.gotoPoint(getX(240), 1740);
                        mv.gotoPoint(getX(570), 1740);
                        groupService.initStep(InitStep.ODIN_DEVANT_GALERIE); // Odin calé, en attente devant la galerie
                        mv.gotoPoint(getX(570), 1130);
                        odinEcranService.displayMessage("Attente calage Nerell");
                        groupService.waitInitStep(InitStep.NERELL_CALAGE_TERMINE); // Attente Nerell calé
                        odinEcranService.displayMessage("Mise en place");
                        mv.gotoPoint(getX(230), 1140);
                        mv.alignFrontTo(getX(345), 537); // Entry point abri de chantier
                    } else {
                        odinEcranService.displayMessage("Mise en place");
                        mv.gotoPoint(getX(240), 1430);
                        mv.alignFrontTo(getX(800), 1700);
                    }
                    groupService.initStep(InitStep.ODIN_EN_POSITION);
                    break;
            }
        } catch (AvoidingException e) {
            odinEcranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
        }

    }

    /**
     * Etape du choix des options + config balise
     */
    private void choixConfig() {
        if (robotStatus.groupOk()) {
            odinEcranService.displayMessage("Attente démarrage Nerell");

            while (!groupService.isReady()) {
                exitFromScreen();
                robotStatus.twoRobots(true);
                avoidingService.setSafeAvoidance(odinEcranService.config().isSafeAvoidance());
                ThreadUtils.sleep(200);
            }

        } else {
            odinEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");

            SignalEdgeFilter manuelRisingEdge = new SignalEdgeFilter(odinEcranService.config().isModeManuel(), Type.RISING);
            SignalEdgeFilter manuelFallingEdge = new SignalEdgeFilter(odinEcranService.config().isModeManuel(), Type.FALLING);

            boolean manuel = odinEcranService.config().isModeManuel();

            while (!io.tirette()) {
                exitFromScreen();

                if (Boolean.TRUE.equals(manuelRisingEdge.filter(odinEcranService.config().isModeManuel()))) {
                    manuel = true;
                    odinEcranService.displayMessage("!!!! Mode manuel !!!!");
                    startMonitoring();
                } else if (manuel && Boolean.TRUE.equals(manuelFallingEdge.filter(odinEcranService.config().isModeManuel()))) {
                    manuel = false;
                    endMonitoring();
                    odinEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");
                }

                // Si on est pas en manuel, gestion de la config
                if (!manuel && !odinEcranService.config().isSkipCalageBordure()) {
                    odinEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");
                }

                robotStatus.twoRobots(odinEcranService.config().isTwoRobots());
                avoidingService.setSafeAvoidance(odinEcranService.config().isSafeAvoidance());

                ThreadUtils.sleep(manuel ? 4000 : 200);
            }
        }
    }
}
