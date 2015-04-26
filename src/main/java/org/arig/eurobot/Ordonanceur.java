package org.arig.eurobot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.IOServices;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.csv.CsvCollector;
import org.arig.robot.exception.I2CException;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Scanner;

/**
 * Created by gdepuille on 08/03/15.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Ordonanceur {

    private static Ordonanceur INSTANCE;

    @Autowired
    private RobotStatus robotStatus;

    @Autowired
    private IOServices ioServices;

    @Autowired
    private II2CManager i2CManager;

    @Autowired
    private SD21Servos servos;

    @Autowired
    private MouvementManager mouvementManager;

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

    public void run() {
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
        log.info("Initialisation servos moteurs");
        servos.printVersion();
        servos.setPositionAndSpeed(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_HAUT, IConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_HAUT, IConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_FERME, IConstantesServos.SPEED_TAPIS);
        servos.setPositionAndSpeed(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_FERME, IConstantesServos.SPEED_TAPIS);
        servos.setPositionAndSpeed(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_FERME, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_FERME, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS, IConstantesServos.SPEED_MONTE_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS, IConstantesServos.SPEED_MONTE_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT, IConstantesServos.SPEED_ASCENSEUR);
        servos.setPositionAndSpeed(IConstantesServos.PINCE, IConstantesServos.PINCE_FERME, IConstantesServos.SPEED_PINCE);
        servos.setPositionAndSpeed(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT, IConstantesServos.SPEED_GUIDE);
        servos.setPositionAndSpeed(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE, IConstantesServos.SPEED_SONAR);

        // Initialisation Mouvement Manager
        log.info("Initialisation du contrôleur de mouvement");
        mouvementManager.init();

        // Activation de la puissance
        //ioServices.enableAlimMoteur();
        //ioServices.enableAlimServoMoteur();

        if (!ioServices.auOk()) {
            log.warn("L'arrêt d'urgence est coupé.");
            while(!ioServices.auOk());
            log.info("Arrêt d'urgence OK");
        }

        // Attente tirette.
        log.info("!!! ... ATTENTE TIRRETTE ... !!!");
        Scanner sc = new Scanner(System.in);
        while(!sc.nextLine().equalsIgnoreCase("start"));

        log.info("Démarrage du match");
        mouvementManager.resetEncodeurs();

        // TODO : A supprimer
        mouvementManager.setVitesse(500L, 800L);
        position.setAngle(conv.degToPulse(90));
        // TODO : FIN A supprimer

        // Activation
        robotStatus.enableAsserv();

        // Match de XX secondes.
        StopWatch matchTime = new StopWatch();
        matchTime.start();
        while(matchTime.getTime() < IConstantesRobot.matchTimeMs) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error("Interruption du Thread", e);
            }
        }
        matchTime.stop();

        // Arrêt de l'asservissement et des moteurs
        robotStatus.disableAsserv();

        // Désactivation de la puissance
        //ioServices.disableAlimMoteur();
        //ioServices.disableAlimServoMoteur();

        if (csvCollector != null) {
            csvCollector.exportToFile();
        }

        log.info("Fin de l'ordonancement du match. Durée {} ms", matchTime.getTime());
    }
}
