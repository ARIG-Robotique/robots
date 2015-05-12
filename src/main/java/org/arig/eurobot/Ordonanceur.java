package org.arig.eurobot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.arig.eurobot.services.IOService;
import org.arig.eurobot.services.ServosService;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.csv.CsvCollector;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.Point;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;

/**
 * Created by gdepuille on 08/03/15.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Ordonanceur {

    private static Ordonanceur INSTANCE;

    @Autowired
    private SD21Servos servos;

    @Autowired
    private RobotStatus robotStatus;

    @Autowired
    private IOService ioService;

    @Autowired
    private II2CManager i2CManager;

    @Autowired
    private ServosService servosService;

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private IPathFinder pathFinder;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired(required = false)
    private CsvCollector csvCollector;

    public static Ordonanceur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ordonanceur();
        }

        return INSTANCE;
    }

    public void run() throws ObstacleFoundException {
        log.info("Demarrage de l'ordonancement du match ...");

        try {
            // Bus I2C
            log.info("Scan I2C ...");
            i2CManager.executeScan();
        } catch (I2CException e) {
            log.error("Erreur lors du scan I2C", e);
            return;
        }

        // Init servos
        log.info("Position initiale des servos moteurs");
        servosService.homes();

        // Initialisation Mouvement Manager
        log.info("Initialisation du contrôleur de mouvement");
        mouvementManager.init();

        // FIXME : Activation de la puissance
        //ioServices.enableAlimMoteur();
        //ioServices.enableAlimServoMoteur();

        if (!ioService.auOk()) {
            log.warn("L'arrêt d'urgence est coupé.");
            ioService.colorAUKo();
            while(!ioService.auOk());
        }
        log.info("Arrêt d'urgence OK");

        if (!ioService.alimMoteurOk() || !ioService.alimServoOk()) {
            log.warn("Alimentation puissance NOK (Moteur : {} ; Servos : {})", ioService.alimMoteurOk(), ioService.alimServoOk());
            while(!ioService.alimMoteurOk() && !ioService.alimServoOk());
        }
        log.info("Alimentation puissance OK (Moteur : {} ; Servos : {})", ioService.alimMoteurOk(), ioService.alimServoOk());

        if (!ioService.tirette()) {
            log.warn("La tirette n'est pas la. Phase de préparation Nerell");
            while(!ioService.tirette()) {
                servosService.checkBtnTapis();
                ioService.equipe();
            }
        }
        log.info("Phase de préparation terminé");

        // Positionnement initiale
        servos.setPosition(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_INIT);
        servos.setPosition(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_INIT);

        mouvementManager.resetEncodeurs();
        robotStatus.enableAsserv();
        mouvementManager.setVitesse(100L, 800L);
        if (robotStatus.getTeam() == Team.JAUNE) {
            position.setPt(new Point(conv.mmToPulse(1000), conv.mmToPulse(460)));
            position.setAngle(conv.degToPulse(90));
        } else {
            position.setPt(new Point(conv.mmToPulse(1000), conv.mmToPulse(3000 - 460)));
            position.setAngle(conv.degToPulse(-90));
        }

        mouvementManager.avanceMM(200);
        mouvementManager.reculeMM(200);
        if (robotStatus.getTeam() == Team.JAUNE) {
            mouvementManager.gotoOrientationDeg(45);
        } else {
            mouvementManager.gotoOrientationDeg(-45);
        }

        log.info("Chargement de la carte");
        pathFinder.construitGraphDepuisImageNoirEtBlanc(new File("./maps/" + robotStatus.getTeam().name().toLowerCase() + ".png"));

        // Attente tirette.
        log.info("!!! ... ATTENTE DEPART TIRRETTE ... !!!");
        while(ioService.tirette());

        // Début du compteur de temps pour le match
        StopWatch matchTime = new StopWatch();
        matchTime.start();

        log.info("Démarrage du match");

        // Activation
        robotStatus.enableMatch();
        robotStatus.enableAvoidance();
        robotStatus.enableAscenseur();

        // Match de XX secondes.
        while(matchTime.getTime() < IConstantesRobot.matchTimeMs) {
            try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException e) {
                log.error("Interruption du Thread", e);
            }
        }
        matchTime.stop();
        log.info("Fin de l'ordonancement du match. Durée {} ms", matchTime.getTime());

        // Arrêt de l'asservissement et des moteurs
        robotStatus.disableAsserv();
        robotStatus.disableAvoidance();
        robotStatus.disableMatch();
        robotStatus.disableAscenseur();

        // Ouverture des servos pour libérer ce que l'on as en stock
        servosService.deposeColonneFinMatch();
        servosService.deposeGobeletDroitFinMatch();
        servosService.deposeGobeletGaucheFinMatch();

        // FIXME : Désactivation de la puissance moteur pour être sur de ne plus rouler
        //ioServices.disableAlimMoteur();

        if (csvCollector != null) {
            csvCollector.exportToFile();
        }

        // On éteint la couleur de la team.
        ioService.clearTeamColor();
    }
}
