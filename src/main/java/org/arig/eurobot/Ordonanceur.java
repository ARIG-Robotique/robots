package org.arig.eurobot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.IOServices;
import org.arig.eurobot.services.ServosServices;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.csv.CsvCollector;
import org.arig.robot.exception.I2CException;
import org.arig.robot.system.MouvementManager;
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
    private ServosServices servosServices;

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
        log.info("Position initiale des servos moteurs");
        servosServices.homes();

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

        if (!ioServices.tirette()) {
            log.warn("La tirette n'est pas la. Phase de préparation Nerell");
            while(!ioServices.tirette()) {
                servosServices.checkBtnTapis();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("Interruption du Thread pendant le check tapis", e);
                }
            }
        }

        // Attente tirette.
        log.info("!!! ... ATTENTE TIRRETTE ... !!!");
        Scanner sc = new Scanner(System.in);
        while(!sc.nextLine().equalsIgnoreCase("start") && ioServices.tirette()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

        log.info("Démarrage du match");
        mouvementManager.resetEncodeurs();

        // TODO : A supprimer
        mouvementManager.setVitesse(200L, 800L);
        position.setAngle(conv.degToPulse(90));
        mouvementManager.gotoPointMM(0, 1500, true);
        // TODO : FIN A supprimer

        // Activation
        robotStatus.enableAsserv();
        robotStatus.enableMatch();

        // Match de XX secondes.
        StopWatch matchTime = new StopWatch();
        matchTime.start();
        while(matchTime.getTime() < IConstantesRobot.matchTimeMs) {
            try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException e) {
                log.error("Interruption du Thread", e);
            }
        }
        matchTime.stop();

        // Arrêt de l'asservissement et des moteurs
        robotStatus.disableAsserv();
        robotStatus.disableMatch();

        // Désactivation de la puissance
        //ioServices.disableAlimMoteur();
        //ioServices.disableAlimServoMoteur();

        if (csvCollector != null) {
            csvCollector.exportToFile();
        }

        log.info("Fin de l'ordonancement du match. Durée {} ms", matchTime.getTime());
    }
}
