package org.arig.prehistobot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.prehistobot.constants.IConstantesRobot;
import org.arig.prehistobot.constants.IConstantesServos;
import org.arig.prehistobot.model.RobotStatus;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.csv.CsvCollector;
import org.arig.robot.exception.I2CException;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Scanner;

/**
 * Created by gdepuille on 08/03/15.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Ordonanceur {

    private static Ordonanceur INSTANCE;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private II2CManager i2CManager;

    @Autowired
    private SD21Servos servos;

    @Autowired
    private MouvementManager mouvementManager;

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
        servos.setPositionAndSpeed(IConstantesServos.SERVO_BRAS_DROIT, IConstantesServos.BRAS_DROIT_HOME, IConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.SERVO_BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_HOME, IConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.SERVO_PORTE_DROITE, IConstantesServos.PORTE_DROITE_CLOSE, IConstantesServos.SPEED_PORTE);
        servos.setPositionAndSpeed(IConstantesServos.SERVO_PORTE_GAUCHE, IConstantesServos.PORTE_GAUCHE_CLOSE, IConstantesServos.SPEED_PORTE);

        // Initialisation Robot Manager
        log.info("Initialisation du contrôleur de mouvement");
        mouvementManager.init();

        // Attente tirette.
        log.info("!!! ... ATTENTE TIRRETTE ... !!!");
        Scanner sc = new Scanner(System.in);
        while(!sc.nextLine().equalsIgnoreCase("start"));

        log.info("Démarrage du match");
        mouvementManager.resetEncodeurs();
        rs.setAsservEnabled(true);

        // Match de 90 secondes.
        StopWatch matchTime = new StopWatch();
        matchTime.start();
        while(matchTime.getTime() < IConstantesRobot.matchTimeMs) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("Interruption du Thread", e);
            }
        }
        matchTime.stop();

        mouvementManager.stop();
        rs.setAsservEnabled(false);

        if (csvCollector != null) {
            csvCollector.exportToFile();
        }

        log.info("Fin de l'ordonancement du match. Durée {} ms", matchTime.getTime());
    }
}
