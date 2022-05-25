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
import org.arig.robot.model.SiteDeRetour;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.ecran.EcranPhoto;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.system.capteurs.CarreFouilleReader;
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
    private BrasService brasService;

    @Autowired
    private CarreFouilleReader carreFouilleReader;

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
//        if (nerellRobotStatus.team() == Team.JAUNE) {
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
        nerellIO.enableLedCapteurCouleur();
        ThreadUtils.sleep(NerellConstantesConfig.WAIT_LED);
        nerellIO.couleurVentouseBas();
        nerellIO.couleurVentouseHaut();
        nerellIO.disableLedCapteurCouleur();

        choixConfig();

        if (!skip) {
            try {
                ThreadUtils.sleep(2000);
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation(20));
                mv.tourneDeg(180);

            } catch (AvoidingException e) {
                nerellEcranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
                throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
            }
        }

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
        // Nope
    }

    @Override
    public void afterMatch() {
        double currentX = mv.currentXMm();
        double currentY = mv.currentYMm();
        if ((currentX <= 500 || currentX >= 2500) && currentY <= 1700 && currentY >= 900) {
            groupService.siteDeRetour(currentY > 1300 ? SiteDeRetour.CAMPEMENT_NORD : SiteDeRetour.CAMPEMENT_SUD);
        }

        nerellIO.releaseAllPompes();
        baliseService.idle();
        nerellRobotStatus.disableBalise();
    }

    @Override
    public void beforePowerOff() {
        nerellIO.disableAllPompes();
        nerellIO.enableAlimServos();

        nerellEcranService.displayMessage("FIN - Enlever la tirette quand stock vide.");

        brasService.setBrasHaut(PositionBras.HORIZONTAL);
        brasService.setBrasBas(PositionBras.HORIZONTAL);

        while (io.tirette()) {
            ThreadUtils.sleep(1000);
        }

        try {
            nerellRobotStatus.ventouseBas(null);
            nerellRobotStatus.ventouseHaut(null);
            while(nerellRobotStatus.stockTaille() > 0){
                nerellRobotStatus.destockage();
            }
            carreFouilleReader.printStateVentouse(null, null);
            carreFouilleReader.printStateStock(null, null, null, null, null, null);
        } catch (Exception e) {
            // NOPE
        }

        brasService.setBrasBas(PositionBras.INIT);
        brasService.setBrasHaut(PositionBras.INIT);
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
            nerellRobotStatus.reverseCarreDeFouille(nerellEcranService.config().hasOption(EurobotConfig.REVERSE_CARRE_FOUILLE));
            nerellRobotStatus.doubleDeposeGalerie(nerellEcranService.config().hasOption(EurobotConfig.DOUBLE_DEPOSE_GALERIE));

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
                    conv.mmToPulse(getX(NerellConstantesConfig.dstCallage)),
                    conv.mmToPulse(1450)
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

                mv.avanceMM(70);
                mv.gotoOrientationDeg(90);

                nerellEcranService.displayMessage("Attente Odin devant la galerie");
                groupService.waitInitStep(InitStep.ODIN_DEVANT_GALERIE); // Odin calé, en attente devant la galerie

                robotStatus.enableCalageBordure(TypeCalage.AVANT_BAS);
                mv.avanceMM(1000);
                robotStatus.enableCalageBordure(TypeCalage.AVANT_BAS);
                mv.avanceMMSansAngle(100);

                if (!io.auOk()) {
                    nerellEcranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
                    throw new ExitProgram(true);
                }

                groupService.initStep(InitStep.NERELL_CALAGE_TERMINE); // Nerell calé

                position.getPt().setY(conv.mmToPulse(EurobotConfig.tableHeight - NerellConstantesConfig.dstCallage));
                position.setAngle(conv.degToPulse(90));

                mv.reculeMM(70);
            }
        } catch (AvoidingException e) {
            nerellEcranService.displayMessage("Erreur lors du calage bordure", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer pour le départ", e);
        }
    }

    /**
     * Positionnement en fonction de la stratégie
     */
    public void positionStrategy() {
        nerellEcranService.displayMessage("Attente Odin en position depart");
        groupService.waitInitStep(InitStep.ODIN_EN_POSITION_BASIC);
        nerellEcranService.displayMessage("Mise en place");

        try {
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            switch (nerellRobotStatus.strategy()) {
                case BASIC:
                default:
                    mv.gotoPoint(getX(240), 1430);
                    mv.alignFrontTo(getX(800), 1700);
                    groupService.initStep(InitStep.NERELL_EN_POSITION_BASIC);
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
