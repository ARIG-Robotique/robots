package org.arig.eurobot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.capteurs.SRF02I2CSonar;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by gdepuille on 07/05/15.
 */
@Slf4j
@Service
public class AvoidanceService implements InitializingBean {

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private SD21Servos sd21;

    @Autowired
    @Qualifier("usFront")
    private SRF02I2CSonar usFront;

    @Autowired
    @Qualifier("usGauche")
    private SRF02I2CSonar usGauche;

    @Autowired
    @Qualifier("usDroit")
    private SRF02I2CSonar usDroit;

    @Autowired
    @Qualifier("usBack")
    private SRF02I2CSonar usBack;

    private int positionServosCounter = 0;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initialisation du service d'évittement d'obstacle");
        usFront.printVersion();
        usDroit.printVersion();
        usGauche.printVersion();
        usBack.printVersion();
    }

    public void process() {
        switch (positionServosCounter) {
            case 0:
                sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE);
                break;
            case 1:
                sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_GAUCHE);
                break;
            case 2:
                sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE);
                break;
            case 3:
                sd21.setPositionAndWait(IConstantesServos.SONAR, IConstantesServos.SONAR_DROITE);
                break;
        }

        // Lecture Sonar
        Future<Integer> frontDistance = usFront.readValue();
        Future<Integer> gaucheDistance = usGauche.readValue();
        Future<Integer> droitDistance = usDroit.readValue();
        Future<Integer> backDistance = usBack.readValue();

        while (!(frontDistance.isDone() && gaucheDistance.isDone() && droitDistance.isDone() && backDistance.isDone()));
        try {
            if (frontDistance.get() <= 30) {
                log.info("Obstacle detecte");

                // 1. Calcul des coordonées

                // 2. Controles inclus sur la table
                boolean isInTable = false;
                // 3. Si inclus, on stop et on met a jour le path
                if (isInTable) {
                    // 3.1 Stop du robot
                    mouvementManager.setObstacleFound(true);

                    // 3.2 Mise à jour de la map du path finding


                    // 3.3 On relance le bouzin
                    mouvementManager.setRestartAfterObstacle(true);
                } else {
                    log.info("Obstacle en dehors de la table, on ne fait rien");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Erreur lors de la récupération des distances : {}", e.toString());
        }
        positionServosCounter++;
        if (positionServosCounter > 3) {
            positionServosCounter = 0;
        }
    }
}
