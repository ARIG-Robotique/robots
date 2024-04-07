package org.arig.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.constants.PamiConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.ExitProgram;
import org.arig.robot.filters.common.ChangeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.InitStep;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.services.PamiRobotServosService;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class PamiOrdonanceur extends AbstractOrdonanceur {

    @Autowired
    private PamiRobotStatus pamiRobotStatus;

    @Autowired
    private PamiIOService pamiIOService;

    @Autowired
    private RobotGroupService groupService;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private PamiRobotServosService pamiServosService;

    private int getX(int x) {
        return tableUtils.getX(pamiRobotStatus.team() == Team.JAUNE, x);
    }

    private int getX(double x) {
        return getX((int) x);
    }

    @Override
    public String getPathfinderMap() {
        return pamiRobotStatus.team().pathfinderMap("odin");
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
        // Nope
    }

    @Override
    public void beforePowerOff() {

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

        boolean done = false;
        do {
            exitFromScreen();
            connectBalise();
            connectGroup();
            if (!robotStatus.twoRobots()) {
                configBalise(updatePhotoFilter, doEtalonnageFilter);
            }

            if (Boolean.TRUE.equals(groupChangeFilter.filter(robotStatus.groupOk()))) {
                // TODO : Sound to Alim board
                if (robotStatus.groupOk()) {

                } else {

                }
            }

            if (robotStatus.groupOk()) {
                done = groupService.isCalage();
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
            /*if (Boolean.TRUE.equals(updatePhotoFilter.filter(pamiEcranService.config().isUpdatePhoto()))) {
                // sur front montant de "updatePhoto" on prend une photo et l'envoie à l'écran
                PhotoResponse photo = baliseService.getPhoto();
                EcranPhoto query = new EcranPhoto();
                query.setMessage(photo == null ? "Erreur inconnue" : photo.getErrorMessage());
                query.setPhoto(photo == null ? null : photo.getData());
                pamiEcranService.updatePhoto(query);

            } else if (Boolean.TRUE.equals(doEtalonnageFilter.filter(pamiEcranService.config().isEtalonnageBalise()))) {
                // sur front montant de "etalonnageBalise" on lance l'étalonnage et envoie le résultat à l'écran
                EtalonnageResponse etalonnage = baliseService.etalonnage();
                EcranPhoto query = new EcranPhoto();
                query.setMessage(etalonnage == null ? "Erreur inconnue" : etalonnage.getErrorMessage());
                query.setPhoto(etalonnage == null ? null : etalonnage.getData());
                pamiEcranService.updatePhoto(query);
            }

            pamiRobotStatus.etalonageBaliseOk(pamiEcranService.config().isEtalonnageOk());*/
        } else {
            pamiRobotStatus.etalonageBaliseOk(false);
        }
    }

    /**
     * Calage sur la bordure
     */
    @Override
    public void calageBordure(boolean skip) {
        //pamiEcranService.displayMessage("Calage bordure");

        try {
            robotStatus.disableAvoidance();
            position.setPt(new Point(
                    conv.mmToPulse(getX(PamiConstantesConfig.dstCallage)),
                    conv.mmToPulse(1785)
            ));
            if (pamiRobotStatus.team() == Team.JAUNE) {
                position.setAngle(conv.degToPulse(0));
            } else {
                position.setAngle(conv.degToPulse(180));
            }
            if (!skip) {
                robotStatus.enableCalageBordure(TypeCalage.ARRIERE);
                mv.reculeMMSansAngle(300);

                position.getPt().setX(conv.mmToPulse(getX(PamiConstantesConfig.dstCallage)));
                if (pamiRobotStatus.team() == Team.JAUNE) {
                    position.setAngle(conv.degToPulse(0));
                } else {
                    position.setAngle(conv.degToPulse(180));
                }

                mv.avanceMM(70);
                mv.gotoOrientationDeg(90);

                robotStatus.enableCalageBordure(TypeCalage.AVANT);
                mv.avanceMM(400);
                robotStatus.enableCalageBordure(TypeCalage.AVANT);
                mv.avanceMMSansAngle(100);

                if (!io.auOk()) {
                    //pamiEcranService.displayMessage("Echappement calage bordure car mauvais sens", LogLevel.ERROR);
                    throw new ExitProgram(true);
                }

                position.getPt().setY(conv.mmToPulse(EurobotConfig.tableHeight - PamiConstantesConfig.dstCallage));
                position.setAngle(conv.degToPulse(90));

                mv.reculeMM(70);
            }
        } catch (AvoidingException e) {
            //pamiEcranService.displayMessage("Erreur lors du calage bordure", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer pour le départ", e);
        }
    }

    /**
     * Positionnement en fonction de la stratégie
     */
    public void positionStrategy() {
        //pamiEcranService.displayMessage("Mise en place");

        try {
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            switch (pamiRobotStatus.strategy()) {
                case FINALE_1:
                    if (!robotStatus.twoRobots()) {
                        mv.gotoPoint(getX(265), 1430);
                        mv.alignFrontTo(getX(750), 1550);
                        break;
                    } else {
                        mv.gotoPoint(getX(240), 1740);
                        mv.gotoPoint(getX(570), 1740);
                        //groupService.initStep(InitStep.ODIN_DEVANT_GALERIE); // Odin calé, en attente devant la galerie
                        mv.gotoPoint(getX(570), 1160);
                        mv.gotoOrientationDeg(pamiRobotStatus.team() == Team.JAUNE ? 0 : 180);
                        //pamiEcranService.displayMessage("Attente calage Nerell");
                        groupService.waitInitStep(InitStep.NERELL_CALAGE_TERMINE); // Attente Nerell calé
                        mv.gotoPoint(getX(robotConfig.distanceCalageArriere() + 20), 1160);
                        mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
                        robotStatus.enableCalageBordure(TypeCalage.ARRIERE);
                        mv.reculeMMSansAngle(30);
                    }
                    //groupService.initStep(InitStep.ODIN_EN_POSITION);
                    break;

                case FINALE_2:
                case BASIC:
                default:
                    if (robotStatus.twoRobots()) {
                        mv.gotoPoint(getX(240), 1740);
                        mv.gotoPoint(getX(570), 1740);
                        //groupService.initStep(InitStep.ODIN_DEVANT_GALERIE); // Odin calé, en attente devant la galerie
                        mv.gotoPoint(getX(570), 1130);
                        //pamiEcranService.displayMessage("Attente calage Nerell");
                        groupService.waitInitStep(InitStep.NERELL_CALAGE_TERMINE); // Attente Nerell calé
                        //pamiEcranService.displayMessage("Mise en place");
                        mv.gotoPoint(getX(230), 1140);
                        mv.alignFrontTo(getX(345), 537); // Entry point abri de chantier
                    } else {
                        //pamiEcranService.displayMessage("Mise en place");
                        mv.gotoPoint(getX(240), 1430);
                        mv.alignFrontTo(getX(800), 1700);
                    }
                    //groupService.initStep(InitStep.ODIN_EN_POSITION);
                    break;
            }
        } catch (AvoidingException e) {
            //pamiEcranService.displayMessage("Erreur lors du calage stratégique", LogLevel.ERROR);
            throw new RuntimeException("Impossible de se placer sur la strategie pour le départ", e);
        }

    }

    /**
     * Etape du choix des options + config balise
     */
    private void choixConfig() {
        if (robotStatus.groupOk()) {
            //pamiEcranService.displayMessage("Attente démarrage Nerell");

            while (!groupService.isReady()) {
                exitFromScreen();
                robotStatus.twoRobots(true);
                //avoidingService.setSafeAvoidance(pamiEcranService.config().isSafeAvoidance());
                ThreadUtils.sleep(200);
            }

        } else {
            //pamiEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");

            //SignalEdgeFilter manuelRisingEdge = new SignalEdgeFilter(pamiEcranService.config().isModeManuel(), Type.RISING);
            //SignalEdgeFilter manuelFallingEdge = new SignalEdgeFilter(pamiEcranService.config().isModeManuel(), Type.FALLING);

            //boolean manuel = pamiEcranService.config().isModeManuel();

            /*while (!io.tirette()) {
                exitFromScreen();

                if (Boolean.TRUE.equals(manuelRisingEdge.filter(pamiEcranService.config().isModeManuel()))) {
                    manuel = true;
                    pamiEcranService.displayMessage("!!!! Mode manuel !!!!");
                    startMonitoring();
                } else if (manuel && Boolean.TRUE.equals(manuelFallingEdge.filter(pamiEcranService.config().isModeManuel()))) {
                    manuel = false;
                    endMonitoring();
                    pamiEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");
                }

                // Si on est pas en manuel, gestion de la config
                if (!manuel && !pamiEcranService.config().isSkipCalageBordure()) {
                    pamiEcranService.displayMessage("Attente mise de la tirette, choix config ou mode manuel");
                }

                robotStatus.twoRobots(pamiEcranService.config().isTwoRobots());
                avoidingService.setSafeAvoidance(pamiEcranService.config().isSafeAvoidance());

                ThreadUtils.sleep(manuel ? 4000 : 200);
            }*/
        }
    }
}
